package com.panhandleirrigation.pivot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
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
import com.panhandleirrigation.pivot.entity.Pivot;
import com.panhandleirrigation.pivot.entity.PivotErrorStatus;
import com.panhandleirrigation.pivot.service.ContactService;
import com.panhandleirrigation.pivot.service.PivotService;

public class PivotTest {

	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test")
	@Sql(scripts = { "classpath:databaseSql/V1.0_Monitoring_Schema.sql",
			"classpath:databaseSql/V1.0_Monitoring_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
	class testsThatDoNotPolluteTheApplicationContext extends BaseTestSupport {

		@Test
		void testGetRequestThatPivotsAreReturn() {
			// Given : a get contacts request with valid customer name

			String uri = String.format("%s/pivots", getBaseURI());

			// When : A valid connection is made

			ResponseEntity<List<Pivot>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : A valid list of contacts is returned
			List<Pivot> actual = response.getBody();
			List<Pivot> expected = buildExpected();

			assertThat(actual).isEqualTo(expected);
		}

		private List<Pivot> buildExpected() {
			List<Pivot> list = new ArrayList<Pivot>();

			// @formatter:off
			
			list.add(Pivot.builder()
					.publicKey("A")
					.pivotName("Shorty")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal("0.253"))
					.build());
			
			list.add(Pivot.builder()
					.publicKey("B")
					.pivotName("NorthOfHouse")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal("0.785"))
					.build());
			list.add(Pivot.builder()
					.publicKey("C")
					.pivotName("SouthOfHouse")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal("254.093"))
					.build());
			list.add(Pivot.builder()
					.publicKey("D")
					.pivotName("NewTnL")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal("123.742"))
					.build());
			list.add(Pivot.builder()
					.publicKey("E")
					.pivotName("BigCircle")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal("327.293"))
					.build());
			// @formatter:on

			Collections.sort(list);

			return list;
		}

		@Test
		void testPutRequestThatPivotIsReturn() {
			// Given : an update contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\" : \"A\",\n"
					+ "    \"pivotName\" : \"Long\",\n"
					+ "    \"errorStatus\" : \"OK\",\n"
					+ "    \"rotation\" : \"10\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Pivot> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : A valid contact is returned
			Pivot pivot = response.getBody();
			assertThat(pivot.getPublicKey()).isEqualTo("A");
			assertThat(pivot.getPivotName()).isEqualTo("Long");
			assertThat(pivot.getErrorStatus()).isEqualTo(PivotErrorStatus.OK);
			assertThat(pivot.getRotation()).isEqualTo(new BigDecimal(10).setScale(3));
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.PivotTest#parametersForPutInvalidRequest")
		void testPutRequestThatBadRequestIsReturnedWithInvalidParameters(String publicKey, String pivotName,
				String errorStatus, String rotation) {
			// Given : an update contact request with invalid parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\" : \"%s\",\n"
					+ "    \"pivotName\" : \"%s\",\n"
					+ "    \"errorStatus\" : \"%s\",\n"
					+ "    \"rotation\" : \"%s\"\n"
					+ "}", publicKey, pivotName, errorStatus, rotation);
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/pivots");
		}

		@Test
		void testPutRequestThatNotFoundIsReturnedWithUnknownParameter() {
			// Given : a update contact request with an unknown customer name
			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\" : \"AA\",\n"
					+ "    \"pivotName\" : \"Long\",\n"
					+ "    \"errorStatus\" : \"OK\",\n"
					+ "    \"rotation\" : \"10\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/pivots");
		}

		@Test
		void testPostRequestThatPivotIsReturn() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\" : \"F\",\n"
					+ "    \"pivotName\" : \"Long\",\n"
					+ "    \"errorStatus\" : \"OK\",\n"
					+ "    \"rotation\" : \"10\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Pivot> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			// And : A valid contact is returned
			Pivot pivot = response.getBody();
			assertThat(pivot.getPublicKey()).isEqualTo("F");
			assertThat(pivot.getPivotName()).isEqualTo("Long");
			assertThat(pivot.getErrorStatus()).isEqualTo(PivotErrorStatus.OK);
			assertThat(pivot.getRotation()).isEqualTo(new BigDecimal(10).setScale(3));
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.PivotTest#parametersForPostInvalidRequest")
		void testPostRequestThatBadRequestIsReturnedWithInvalidParameters(String publicKey, String pivotName,
				String errorStatus, String rotation) {
			// Given : a create contact request with invalid parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"publicKey\" : \"%s\",\n"
					+ "    \"pivotName\" : \"%s\",\n"
					+ "    \"errorStatus\" : \"%s\",\n"
					+ "    \"rotation\" : \"%s\"\n"
					+ "}", publicKey, pivotName, errorStatus, rotation);
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

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
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/pivots");
		}

		@Test
		void testDeleteRequestThatNoContentStatusIsReturned() {
			// Given : a delete contact request with valid parameters
			String uri = String.format("%s/pivots?pivotKey=%s", getBaseURI(), "B");

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
			String uri = String.format("%s/pivots?pivotKey=%s", getBaseURI(), "*(&(*&");

			// When : A valid connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/pivots");
		}

		@Test
		void testDeleteRequestThatNotFoundIsReturnedWithUnknownName() {
			// Given : a delete contact request with invalid parameters
			String uri = String.format("%s/pivots?pivotKey=%s", getBaseURI(), "AA");

			// When : A valid connection is made
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/pivots");
		}

	}

	static Stream<Arguments> parametersForPutInvalidRequest() {
		// @formatter:off
		//String publicKey, String pivotName, String errorStatus, String rotation
		return Stream.of(
			//pivot key
			arguments("$%^&*(", "Long", "OK", "10"),
			
			//pivot name
			arguments("A", "%^&*(", "OK", "10"),
			
			//Error status
			arguments("A", "Long", "BadStatus", "10"),
			
			//Rotation
			arguments("A", "Long", "OK", "&^*&^*&^")
		);
		// @formatter:on
	}

	static Stream<Arguments> parametersForPostInvalidRequest() {
		// @formatter:off
		//String publicKey, String pivotName, String errorStatus, String rotation
		return Stream.of(
			//pivot key
			arguments("$%^&*(", "Long", "OK", "10"),
			
			//pivot name
			arguments("A", "%^&*(", "OK", "10"),
			
			//Error status
			arguments("A", "Long", "BadStatus", "10"),
			
			//Rotation
			arguments("A", "Long", "OK", "&^*&^*&^")
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
		PivotService pivotService;

		@Test
		void testGetRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a get pivots request
			String uri = String.format("%s/pivots", getBaseURI());

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(pivotService).fetchPivots();

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/pivots");
		}

		@Test
		void testPutRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : an update contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\" : \"A\",\n"
					+ "    \"pivotName\" : \"Long\",\n"
					+ "    \"errorStatus\" : \"OK\",\n"
					+ "    \"rotation\" : \"10\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made but an error happens
			// @formatter:off
			Pivot errorBody = Pivot.builder()
					.publicKey("A")
					.pivotName("Long")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal(10))
					.build();
			// @formatter:on

			doThrow(new RuntimeException("Custom Error")).when(pivotService).updatePivot(errorBody);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/pivots");
		}

		@Test
		void testPostRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"publicKey\" : \"F\",\n"
					+ "    \"pivotName\" : \"Long\",\n"
					+ "    \"errorStatus\" : \"OK\",\n"
					+ "    \"rotation\" : \"10\"\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/pivots", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made but an error happens
			// @formatter:off
			Pivot errorBody = Pivot.builder()
					.publicKey("F")
					.pivotName("Long")
					.errorStatus(PivotErrorStatus.OK)
					.rotation(new BigDecimal(10))
					.build();
			// @formatter:on

			doThrow(new RuntimeException("Custom Error")).when(pivotService).createPivot(errorBody);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/pivots");
		}

		@Test
		void testDeleteRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a delete contact request with valid parameters

			String uri = String.format("%s/pivots?pivotKey=%s", getBaseURI(), "B");

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(pivotService).deletePivot("B");

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/pivots");
		}
	}

}
