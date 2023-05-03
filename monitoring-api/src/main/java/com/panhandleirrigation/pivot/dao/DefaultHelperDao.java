package com.panhandleirrigation.pivot.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.panhandleirrigation.pivot.entity.TableName;
import com.panhandleirrigation.pivot.entity.ValidKeyGenerated;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultHelperDao implements HelperDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public ValidKeyGenerated verifyKey(String key, TableName tableName, String column) {
		// @formatter:off
		String sql = String.format(""
				+ "SELECT COUNT(1) as isFound "
				+ "FROM %s "
				+ "WHERE :column = :key "
				+ "LIMIT 1;", tableName.toString());
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		//params.put("tableName", table);
		params.put("column", column);
		params.put("key", key);

		return jdbcTemplate.query(sql, params, new ResultSetExtractor<ValidKeyGenerated>() {

			@Override
			public ValidKeyGenerated extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return ValidKeyGenerated.values()[rs.getInt("isFound")];
			}
		});
	}

}
