package com.panhandleirrigation.pivot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.panhandleirrigation.pivot.entity.Customer;
import com.panhandleirrigation.pivot.service.CustomerService;

class CustomerTest {

	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test")
	@Sql(scripts = { "classpath:databaseSql/V1.0_Monitoring_Schema.sql",
			"classpath:databaseSql/V1.0_Monitoring_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
	class testsThatDoNotPolluteTheApplicationContext extends BaseTestSupport {

		@Test
		void testGetRequestThatCustomerListIsReturned() {
			// Given : a request for a customer list
			String uri = String.format("%s/customers", getBaseURI());

			// When : a connection is made
			ResponseEntity<List<Customer>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that the returned list is correct
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : returns a list of all customers
			List<Customer> actual = response.getBody();
			List<Customer> expected = buildExpected();

			assertThat(actual).isEqualTo(expected);
		}

		List<Customer> buildExpected() {
			List<Customer> list = new ArrayList<>();

			// @formatter:off
			
			list.add(Customer.builder()
					.customerName("Bob")
					.publicKey("A")
					.build());
			
			list.add(Customer.builder()
					.customerName("Fred")
					.publicKey("B")
					.build());
			
			list.add(Customer.builder()
					.customerName("Jones")
					.publicKey("D")
					.build());
			
			list.add(Customer.builder()
					.customerName("Steve")
					.publicKey("C")
					.build());
			
			
	 
			// @formatter:on

			Collections.sort(list);

			return list;
		}

		@Test
		void testThatCustomerIsUpdated() {
			// Given : a put request to update a customer

			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\": \"C\",\n"
					+ "    \"customerName\": \"Tom\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/customers", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : a connection is made
			ResponseEntity<Customer> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity, Customer.class);

			// Then : assert that ok status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : the returned customer is correct
			assertThat(response.getBody()).isNotNull();

			Customer cust = response.getBody();
			assertThat(cust.getPublicKey()).isEqualTo("C");
			assertThat(cust.getCustomerName()).isEqualTo("Tom");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.CustomerTest#parametersForInvalidPutRequest")
		void testThatBadRequestIsReturnedForInvalidParameters(String targetKey, String NewName) {
			// Given : Bad Parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\": \"%s\",\n"
					+ "    \"customerName\": \"%s\"\n"
					+ "}", targetKey, NewName);
			// @formatter:on

			String uri = String.format("%s/customers", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that the correct error is returned
			assertErrorMessageValid(response, HttpStatus.BAD_REQUEST, "/customers");

		}

		@Test
		void testThatNotFoundIsReturnedForUnknownTarget() {
			// Given : Bad Parameters
			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\": \"UnknownKey\",\n"
					+ "    \"customerName\": \"Tom\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/customers", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that the correct error is returned
			assertErrorMessageValid(response, HttpStatus.NOT_FOUND, "/customers");
		}
	}

	static Stream<Arguments> parametersForInvalidPutRequest() {
		// String targetKey, String NewName
		// @formatter:off
		return Stream.of(
				arguments("C", "&*^*&^*"), 
				arguments("%&^%^%", "Bob")
		);
		// @formatter:on		
	}

	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test")
	@Sql(scripts = { "classpath:databaseSql/V1.0_Monitoring_Schema.sql",
			"classpath:databaseSql/V1.0_Monitoring_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
	class testsThatPolluteTheApplicationContext extends BaseTestSupport {

		@MockBean
		private CustomerService customerService;

		@Test
		void testThatGetRequestUnplannedErrorReturnsInternalServerError() {
			// Given : an invalid request for a customer list
			String uri = String.format("%s/customers", getBaseURI());

			doThrow(new RuntimeException("Custom Error")).when(customerService).fetchCustomers();

			// When : a connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that the correct error is returned
			assertErrorMessageValid(response, HttpStatus.INTERNAL_SERVER_ERROR, "/customers");
		}

		@Test
		void testThatPutRequestUnplannedErrorReturnsInternalServerError() {
			// Given : an invalid update for a customer
			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\": \"C\",\n"
					+ "    \"customerName\": \"Tom\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/customers", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			Customer testData = Customer.builder().publicKey("C").customerName("Tom").build();

			doThrow(new RuntimeException("Custom Error")).when(customerService).updateCustomer(testData);

			// When : a connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that the correct error is returned
			assertErrorMessageValid(response, HttpStatus.INTERNAL_SERVER_ERROR, "/customers");
		}
	}

}
