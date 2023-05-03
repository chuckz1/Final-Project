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

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.CustomerPivot;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultCustomerPivotDao implements CustomerPivotDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<CustomerPivot> CustomerPivotsByCustomerPK(Long customerPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM customer_pivots "
				+ "WHERE customer_fk = :customerPK";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPK", customerPK);

		return jdbcTemplate.query(sql, params, new RowMapper<>() {

			@Override
			public CustomerPivot mapRow(ResultSet rs, int rowNum) throws SQLException {
				// @formatter:off
				return CustomerPivot.builder()
						.publicKey(rs.getString("public_key"))
						.customerFK(rs.getLong("customer_fk"))
						.pivotFK(rs.getLong("pivot_fk"))
						.build();
				// @formatter:on
			}
		});
	}

	@Override
	public Optional<CustomerPivot> getCustomerPivotByPK(String customerPivotPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM customer_pivots "
				+ "WHERE public_key = :customerPivotPK "
				+ "LIMIT 1";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPivotPK", customerPivotPK);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<CustomerPivot>() {

			@Override
			public CustomerPivot extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return CustomerPivot.builder()
						.publicKey(rs.getString("public_key"))
						.customerFK(rs.getLong("customer_fk"))
						.pivotFK(rs.getLong("pivot_fk"))
						.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<CustomerPivot> createCustomerPivot(CustomerPivot customerPivot) {
		// @formatter:off
		String sql = ""
				+ "INSERT INTO customer_pivots ("
				+ "customer_fk, pivot_fk, public_key"
				+ ") VALUES ("
				+ ":customerFK, :pivotFK, :publicKey"
				+ ")";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerFK", customerPivot.getCustomerFK());
		params.put("pivotFK", customerPivot.getPivotFK());
		params.put("publicKey", customerPivot.getPublicKey());

		jdbcTemplate.update(sql, params);

		return getCustomerPivotByPK(customerPivot.getPublicKey());
	}

	@Override
	public int deleteCustomerPivot(String customerPivotKey) {
		// @formatter:off
		String sql = ""
				+ "DELETE FROM "
				+ "customer_pivots "
				+ "WHERE public_key = :customerPivotKey ";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPivotKey", customerPivotKey);

		// delete contact
		return jdbcTemplate.update(sql, params);
	}

}
