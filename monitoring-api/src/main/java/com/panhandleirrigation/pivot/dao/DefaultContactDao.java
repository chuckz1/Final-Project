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
import org.springframework.stereotype.Service;

import com.panhandleirrigation.pivot.entity.Contact;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultContactDao implements ContactDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<Contact> getContactsByCustomerFK(Long customerPK) {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM contacts "
				+ "WHERE customer_fk = :customerPK";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPK", customerPK);

		return jdbcTemplate.query(sql, params, new RowMapper<>() {

			@Override
			public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
				// @formatter:off
				return Contact.builder()
						.contactPK(rs.getLong("contact_pk"))
						.customerFK(rs.getLong("customer_fk"))
						.description(rs.getString("description"))
						.email(rs.getString("email"))
						.build();
				// @formatter:on
			}
		});
	}

	@Override
	public Optional<Contact> getContactByCustomerFKandIndex(Long customerPK, int contactIndex) {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM contacts "
				+ "WHERE customer_fk = :customer_pk "
				+ "AND contact_index = :contactIndex";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customer_pk", customerPK);
		params.put("contactIndex", contactIndex);

		return Optional.of(jdbcTemplate.query(sql, params, new ResultSetExtractor<Contact>() {

			@Override
			public Contact extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return Contact.builder()
						.contactPK(rs.getLong("contact_pk"))
						.customerFK(rs.getLong("customer_fk"))
						.contactIndex(rs.getInt("contact_index"))
						.description(rs.getString("description"))
						.email(rs.getString("email"))
						.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<Contact> updateContact(Contact target) {
		// @formatter:off
		String sql = ""
				+ "UPDATE contacts "
				+ "SET customer_fk = :customer_fk, "
				+ "description = :description, "
				+ "email = :email "
				+ "WHERE contact_pk = :contact_pk";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customer_fk", target.getCustomerFK());
		params.put("description", target.getDescription());
		params.put("email", target.getEmail());
		params.put("contact_pk", target.getContactPK());
		
		jdbcTemplate.update(sql, params);

		return getContactByPK(target.getContactPK());
	}

	@Override
	public Optional<Contact> getContactByPK(Long key) {
		// @formatter:off
		String sql = ""
				+ "SELECT * "
				+ "FROM contacts "
				+ "WHERE contact_pk = :key "
				+ "LIMIT 1";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("key", key);

		return Optional.ofNullable(jdbcTemplate.query(sql, params, new ResultSetExtractor<Contact>() {

			@Override
			public Contact extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				// @formatter:off
				return Contact.builder()
					.contactPK(rs.getLong("contact_pk"))
					.contactIndex(rs.getInt("contact_index"))
					.customerFK(rs.getLong("customer_fk"))
					.description(rs.getString("description"))
					.email(rs.getString("email"))
					.build();
				// @formatter:on
			}
		}));
	}

	@Override
	public Optional<Contact> createContact(Contact contact) {
		// @formatter:off
		String sql = ""
				+ "INSERT INTO contacts ("
				+ "customer_fk, contact_index, description, email"
				+ ") VALUES ("
				+ ":customer_fk, :contact_index, :description, :email"
				+ ")";
		// @formatter:on

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("customer_fk", contact.getCustomerFK());
		params.addValue("contact_index", contact.getContactIndex());
		params.addValue("description", contact.getDescription());
		params.addValue("email", contact.getEmail());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(sql, params, keyHolder);

		Long contactID = keyHolder.getKey().longValue();

		return getContactByPK(contactID);
	}

	@Override
	public int deleteContact(Long customerPK, int contactIndex) {
		// @formatter:off
		String sql = ""
				+ "DELETE FROM "
				+ "contacts "
				+ "WHERE customer_fk = :customerPK "
				+ "AND contact_index = :contactIndex";
		// @formatter:on

		Map<String, Object> params = new HashMap<>();
		params.put("customerPK", customerPK);
		params.put("contactIndex", contactIndex);

		// delete contact
		return jdbcTemplate.update(sql, params);
	}

}
