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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.panhandleirrigation.pivot.entity.Customer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultCustomerDao implements CustomerDao {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<Customer> fetchCustomers() {
		log.info("DAO: Request for customer list");

		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM customers";
		// @formatter:on

		return jdbcTemplate.query(sql, new RowMapper<>() {

			@Override
			public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
				// @formatter:off
				return Customer.builder()
						.customerPK(rs.getLong("customer_pk"))
						.publicKey(rs.getString("customer_key"))
						.customerName(rs.getString("customer_name"))
						.build();
				// @formatter:on

			}
		});
	}

	@Override
	public Optional<Customer> getCustomerFromKey(String targetKey) {
		// @formatter:off
		String sql = ""
				+ "SELECT * " 
				+ "FROM customers " 
				+ "WHERE customer_key = :targetKey " 
				+ "LIMIT 1";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("targetKey", targetKey);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Customer>() {

			@Override
			public Customer extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return Customer.builder()
						.customerPK(rs.getLong("customer_pk"))
						.publicKey(rs.getString("customer_key"))
						.customerName(rs.getString("customer_name"))
						.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<Customer> getCustomerFromName(String targetName) {
		// TODO Auto-generated method stub
		// @formatter:off
		String sql = ""
				+ "SELECT * " 
				+ "FROM customers " 
				+ "WHERE customer_name = :targetName " 
				+ "LIMIT 1";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("targetName", targetName);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Customer>() {

			@Override
			public Customer extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return Customer.builder()
						.customerPK(rs.getLong("customer_pk"))
						.publicKey(rs.getString("customer_key"))
						.customerName(rs.getString("customer_name"))
						.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<Customer> updateCustomer(Customer customer) {
		log.info("DAO: Atempting to update customer");

		// @formatter:off
		String sql = ""
				+ "UPDATE customers "
				+ "SET customer_name = :newName "
				+ "WHERE customer_key = :publicKey;";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("newName", customer.getCustomerName());
		params.put("publicKey", customer.getPublicKey());

		// update database
		jdbcTemplate.update(sql, params);

		return getCustomerFromKey(customer.getPublicKey());
	}

	@Override
	public Optional<Long> convertKeyToPK(String publicKey) {
		// @formatter:off
		String sql = ""
				+ "SELECT customer_pk "
				+ "FROM customers "
				+ "WHERE customer_key = :publicKey";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("publicKey", publicKey);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getLong("customer_pk");
			}
		}));
	}

	@Override
	public Optional<String> convertPKtoString(Long customerPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT customer_key "
				+ "FROM customers "
				+ "WHERE customer_pk = :customerPK";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPK", customerPK);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getString("customer_key");
			}
		}));
	}
}
