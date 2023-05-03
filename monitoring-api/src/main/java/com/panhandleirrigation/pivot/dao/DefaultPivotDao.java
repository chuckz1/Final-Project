package com.panhandleirrigation.pivot.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.panhandleirrigation.pivot.entity.Customer;
import com.panhandleirrigation.pivot.entity.Pivot;
import com.panhandleirrigation.pivot.entity.PivotErrorStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultPivotDao implements PivotDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<Pivot> fetchPivots() {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM pivots";
		// @formatter:on

		return jdbcTemplate.query(sql, new RowMapper<>() {

			@Override
			public Pivot mapRow(ResultSet rs, int rowNum) throws SQLException {
				// @formatter:off
				return Pivot.builder()
						.pivotPK(rs.getLong("pivot_pk"))
						.publicKey(rs.getString("pivot_key"))
						.pivotName(rs.getString("pivot_name"))
						.errorStatus(PivotErrorStatus.valueOf(rs.getString("error_status")))
						.rotation(rs.getBigDecimal("rotation"))
						.build();
				// @formatter:on
			}
		});
	}

	@Override
	public Optional<Pivot> getPivotByPK(Long pivotPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT * " 
				+ "FROM pivots " 
				+ "WHERE pivot_pk = :pivotPK " 
				+ "LIMIT 1";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("pivotPK", pivotPK);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Pivot>() {

			@Override
			public Pivot extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return Pivot.builder()
						.pivotPK(rs.getLong("pivot_pk"))
						.publicKey(rs.getString("pivot_key"))
						.pivotName(rs.getString("pivot_name"))
						.errorStatus(PivotErrorStatus.valueOf(rs.getString("error_status")))
						.rotation(rs.getBigDecimal("rotation"))
						.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<Pivot> updatePivot(Pivot pivot) {
		// @formatter:off
		String sql = ""
				+ "UPDATE pivots "
				+ "SET pivot_name = :pivotName, "
				+ "error_status = :errorStatus, "
				+ "rotation = :rotation "
				+ "WHERE pivot_pk = :pivotPK";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("pivotName", pivot.getPivotName());
		params.put("errorStatus", pivot.getErrorStatus().toString());
		params.put("rotation", pivot.getRotation());
		params.put("pivotPK", pivot.getPivotPK());
		
		jdbcTemplate.update(sql, params);

		return getPivotByPK(pivot.getPivotPK());
	}

	@Override
	public Optional<Long> convertKeyToPK(String publicKey) {
		// @formatter:off
		String sql = ""
				+ "SELECT pivot_pk "
				+ "FROM pivots "
				+ "WHERE pivot_key = :publicKey";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("publicKey", publicKey);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getLong("pivot_pk");
			}
		}));
	}

	@Override
	public Optional<String> convertPKtoString(Long pivotPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT pivot_key "
				+ "FROM pivots "
				+ "WHERE pivot_pk = :pivotPK";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("pivotPK", pivotPK);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getString("pivot_key");
			}
		}));
	}

	@Override
	public Optional<Pivot> createPivot(Pivot pivot) {
		// @formatter:off
		String sql = ""
				+ "INSERT INTO pivots ("
				+ "pivot_pk, pivot_key, pivot_name, error_status, rotation"
				+ ") VALUES ("
				+ ":pivotPK, :publicKey, :pivotName, :errorStatus, :rotation"
				+ ")";
		// @formatter:on

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pivotPK", pivot.getPivotPK());
		params.addValue("publicKey", pivot.getPublicKey());
		params.addValue("pivotName", pivot.getPivotName());
		params.addValue("errorStatus", pivot.getErrorStatus().toString());
		params.addValue("rotation", pivot.getRotation());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params, keyHolder);

		Long pivotPK = keyHolder.getKey().longValue();

		return getPivotByPK(pivotPK);
	}

	@Override
	public int deletePivot(Long pivotPK) {
		// @formatter:off
		String sql = ""
				+ "DELETE FROM "
				+ "pivots "
				+ "WHERE pivot_pk = :pivotPK ";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("pivotPK", pivotPK);

		// delete contact
		return jdbcTemplate.update(sql, params);
	}

}
