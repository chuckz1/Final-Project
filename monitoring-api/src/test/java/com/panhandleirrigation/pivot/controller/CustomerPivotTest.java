package com.panhandleirrigation.pivot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;
import com.panhandleirrigation.pivot.entity.CustomerPivot;
import com.panhandleirrigation.pivot.service.ContactService;
import com.panhandleirrigation.pivot.service.CustomerPivotService;

public class CustomerPivotTest {

	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test")
	@Sql(scripts = { "classpath:databaseSql/V1.0_Monitoring_Schema.sql",
			"classpath:databaseSql/V1.0_Monitoring_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
	class testsThatDoNotPolluteTheApplicationContext extends BaseTestSupport {

		@Test
		void testGetRequestThatCustomerPivotsIsReturn() {
			// Given : a get contacts request with valid customer name

			String uri = String.format("%s/customer_pivots?customerKey=%s", getBaseURI(), "A");

			// When : A valid connection is made

			ResponseEntity<List<CustomerPivot>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : A valid list of contacts is returned
			List<CustomerPivot> actual = response.getBody();
			List<CustomerPivot> expected = buildExpected();

			assertThat(actual).isEqualTo(expected);
		}

		private List<CustomerPivot> buildExpected() {
			List<CustomerPivot> list = new ArrayList<CustomerPivot>();

			// @formatter:off
			
			list.add(CustomerPivot.builder()
					.publicKey("A")
					.customerKey("A")
					.pivotKey("B")
					.build());
			
			list.add(CustomerPivot.builder()
					.publicKey("B")
					.customerKey("A")
					.pivotKey("C")
					.build());
	 
			// @formatter:on

			Collections.sort(list);

			return list;
		}

		@Test
		void testGetRequestThatBadRequestIsReturnedWithInvalidParameters() {
			// Given : a get contacts request with an invalid customer name
			String uri = String.format("%s/customer_pivots?customerKey=%s", getBaseURI(), "*^*&*)(*");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/customer_pivots");
		}

		@Test
		void testGetRequestThatNotFoundIsReturnedWithUnknownKey() {
			// Given : a get contacts request with an unknown customer name
			String uri = String.format("%s/customer_pivots?customerKey=%s", getBaseURI(), "UnknownKey");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/customer_pivots");
		}

		@Test
		void testPostRequestThatContactIsReturn() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\": \"%s\",\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"pivotKey\": \"%s\"\n"
					+ "}", "K", "C", "E");
			// @formatter:on

			String uri = String.format("%s/customer_pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<CustomerPivot> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			// And : A valid contact is returned
			CustomerPivot customerPivot = response.getBody();
			assertThat(customerPivot.getPublicKey()).isEqualTo("K");
			assertThat(customerPivot.getCustomerKey()).isEqualTo("C");
			assertThat(customerPivot.getPivotKey()).isEqualTo("E");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.CustomerPivotTest#parametersForPostInvalidRequest")
		void testPostRequestThatBadRequestIsReturnedWithInvalidParameters(String publicKey, String customerKey,
				String pivotKey) {
			// Given : a create contact request with invalid parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\": \"%s\",\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"pivotKey\": \"%s\"\n"
					+ "}", publicKey, customerKey, pivotKey);
			// @formatter:on

			String uri = String.format("%s/customer_pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/customer_pivots");
		}

		@Test
		void testPostRequestThatNotFoundIsReturnedWithUnknownParameter() {
			// Given : a update contact request with an unknown customer name
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\": \"%s\",\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"pivotKey\": \"%s\"\n"
					+ "}", "K", "UnknownKey", "E");
			// @formatter:on

			String uri = String.format("%s/customer_pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/customer_pivots");
		}

		@Test
		void testDeleteRequestThatNoContentStatusIsReturned() {
			// Given : a delete contact request with valid parameters

			String uri = String.format("%s/customer_pivots?customerPivotKey=%s", getBaseURI(), "C");

			// When : A valid connection is made

			ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : A no content status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@Test
		void testDeleteRequestThatBadRequestIsReturnedWithInvalidParameters() {
			// Given : a delete contact request with invalid parameters
			String uri = String.format("%s/customer_pivots?customerPivotKey=%s", getBaseURI(), "*(&(*&*(");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/customer_pivots");
		}

		@Test
		void testPostRequestThatNotFoundIsReturnedWithUnknownName() {
			// Given : a delete contact request with invalid parameters
			String uri = String.format("%s/customer_pivots?customerPivotKey=%s", getBaseURI(), "UnknownKey");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/customer_pivots");
		}

	}

	static Stream<Arguments> parametersForPostInvalidRequest() {
		// @formatter:off
		//String publicKey, String customerKey, String pivotKey
		return Stream.of(
			//public key
			arguments("*&&(*&", "C", "E"),
			
			//customerKey
			arguments("K", "*&(*&", "E"),
			
			//pivotKey
			arguments("K", "C", "*(&(*")
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
		CustomerPivotService customerPivotService;

		@Test
		void testGetRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a get contacts request
			String uri = String.format("%s/customer_pivots?customerKey=%s", getBaseURI(), "A");

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(customerPivotService)
					.fetchCustomerPivotsByCustomerKey("A");

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/customer_pivots");
		}

		@Test
		void testPostRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\": \"%s\",\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"pivotKey\": \"%s\"\n"
					+ "}", "K", "C", "E");
			// @formatter:on

			String uri = String.format("%s/customer_pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made but an error happens
			// @formatter:off
			CustomerPivot errorBody = CustomerPivot.builder()
					.publicKey("K")
					.customerKey("C")
					.pivotKey("E")
					.build();
			// @formatter:on

			doThrow(new RuntimeException("Custom Error")).when(customerPivotService).createCustomerPivot(errorBody);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/customer_pivots");
		}

		@Test
		void testDeleteRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a delete contact request with valid parameters

			String uri = String.format("%s/customer_pivots?customerPivotKey=%s", getBaseURI(), "C");

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(customerPivotService).deleteCustomerPivot("C");

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/customer_pivots");
		}
	}
}
