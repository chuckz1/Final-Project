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

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;
import com.panhandleirrigation.pivot.service.ContactService;

class ContactTest {

	@Nested
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@ActiveProfiles("test")
	@Sql(scripts = { "classpath:databaseSql/V1.0_Monitoring_Schema.sql",
			"classpath:databaseSql/V1.0_Monitoring_Data.sql" }, config = @SqlConfig(encoding = "utf-8"))
	class testsThatDoNotPolluteTheApplicationContext extends BaseTestSupport {

		@Test
		void testGetRequestThatContactIsReturn() {
			// Given : a get contacts request with valid customer name

			String uri = String.format("%s/contacts?customerKey=%s", getBaseURI(), "A");

			// When : A valid connection is made

			ResponseEntity<List<Contact>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : A valid list of contacts is returned
			List<Contact> actual = response.getBody();
			List<Contact> expected = buildExpected();

			assertThat(actual).isEqualTo(expected);
		}

		private List<Contact> buildExpected() {
			List<Contact> list = new ArrayList<Contact>();

			// @formatter:off
			
			list.add(Contact.builder()
					.customerFK(1L)
					.description("Personal Email")
					.email("bob@gmail.org")
					.build());
			
			list.add(Contact.builder()
					.customerFK(1L)
					.description("Work Email")
					.email("bob@banking.net")
					.build());
	 
			// @formatter:on

			Collections.sort(list);

			return list;
		}

		@Test
		void testGetRequestThatBadRequestIsReturnedWithInvalidParameters() {
			// Given : a get contacts request with an invalid customer name
			String uri = String.format("%s/contacts?customerKey=%s", getBaseURI(), "*^*&*)(*");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/contacts");
		}

		@Test
		void testGetRequestThatNotFoundIsReturnedWithUnknownKey() {
			// Given : a get contacts request with an unknown customer name
			String uri = String.format("%s/contacts?customerKey=%s", getBaseURI(), "UnknownKey");

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/contacts");
		}

		@Test
		void testPutRequestThatContactIsReturn() {
			// Given : an update contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"customerKey\": \"A\",\n"
					+ "    \"contact\": {\n"
					+ "        \"contactIndex\": 1,\n"
					+ "        \"description\": \"Old Email\",\n"
					+ "        \"email\": \"bobby@banking.net\"\n"
					+ "    }\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Contact> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			// And : A valid contact is returned
			Contact contact = response.getBody();
			assertThat(contact.getContactIndex()).isEqualTo(1);
			assertThat(contact.getDescription()).isEqualTo("Old Email");
			assertThat(contact.getEmail()).isEqualTo("bobby@banking.net");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.ContactTest#parametersForPutInvalidRequest")
		void testPutRequestThatBadRequestIsReturnedWithInvalidParameters(String customerKey, int contactIndex,
				String newDescription, String newEmail) {
			// Given : an update contact request with invalid parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"contact\": {\n"
					+ "        \"contactIndex\": %d,\n"
					+ "        \"description\": \"%s\",\n"
					+ "        \"email\": \"%s\"\n"
					+ "    }\n"
					+ "}", customerKey, contactIndex, newDescription, newEmail);
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

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
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/contacts");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.ContactTest#parametersForPutUnknownRequest")
		void testPutRequestThatNotFoundIsReturnedWithUnknownParameter(String customerKey, int contactIndex) {
			// Given : a update contact request with an unknown customer name
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"customerKey\": \"%s\",\n"
					+ "    \"contact\": {\n"
					+ "        \"contactIndex\": %d,\n"
					+ "        \"description\": \"Old Email\",\n"
					+ "        \"email\": \"bobby@banking.net\"\n"
					+ "    }\n"
					+ "}", customerKey, contactIndex);
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

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
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/contacts");
		}

		@Test
		void testPostRequestThatContactIsReturn() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"customerKey\" : \"B\",\n"
					+ "    \"contact\" : {\n"
					+ "        \"contactIndex\": 2,\n"
					+ "        \"description\" : \"Personal Email\",\n"
					+ "        \"email\" : \"fred@outlook.com\"\n"
					+ "    }\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made

			ResponseEntity<Contact> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : An OK status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			// And : A valid contact is returned
			Contact contact = response.getBody();
			assertThat(contact.getContactIndex()).isEqualTo(2);
			assertThat(contact.getDescription()).isEqualTo("Personal Email");
			assertThat(contact.getEmail()).isEqualTo("fred@outlook.com");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.ContactTest#parametersForPostInvalidRequest")
		void testPostRequestThatBadRequestIsReturnedWithInvalidParameters(String customerKey, int contactIndex,
				String description, String email) {
			// Given : a create contact request with invalid parameters
			// @formatter:off
			String body = String.format("{\n"
					+ "    \"customerKey\" : \"%s\",\n"
					+ "    \"contact\" : {\n"
					+ "        \"contactIndex\": %d,\n"
					+ "        \"description\" : \"%s\",\n"
					+ "        \"email\" : \"%s\"\n"
					+ "    }\n"
					+ "}", customerKey, contactIndex, description, email);
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

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
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/contacts");
		}

		@Test
		void testPostRequestThatNotFoundIsReturnedWithUnknownParameter() {
			// Given : a update contact request with an unknown customer name
			// @formatter:off
			String body = "{\n"
					+ "    \"customerKey\" : \"UnknownKey\",\n"
					+ "    \"contact\" : {\n"
					+ "        \"contactIndex\": 2,\n"
					+ "        \"description\" : \"Personal Email\",\n"
					+ "        \"email\" : \"fred@outlook.com\"\n"
					+ "    }\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

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
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/contacts");
		}

		@Test
		void testDeleteRequestThatNoContentStatusIsReturned() {
			// Given : a delete contact request with valid parameters

			String uri = String.format("%s/contacts?customerKey=%s&contactIndex=%d", getBaseURI(), "C", 1);

			// When : A valid connection is made

			ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : A no content status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.ContactTest#parametersForDeleteInvalidRequest")
		void testDeleteRequestThatBadRequestIsReturnedWithInvalidParameters(String customerKey, int contactKey) {
			// Given : a delete contact request with invalid parameters
			String uri = String.format("%s/contacts?customerKey=%s&contactIndex=%d", getBaseURI(), customerKey,
					contactKey);

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.BAD_REQUEST, "/contacts");
		}

		@ParameterizedTest
		@MethodSource("com.panhandleirrigation.pivot.controller.ContactTest#parametersForPostUnknownRequest")
		void testPostRequestThatNotFoundIsReturnedWithUnknownName(String customerKey, int contactIndex) {
			// Given : a delete contact request with invalid parameters
			String uri = String.format("%s/contacts?customerKey=%s&contactIndex=%d", getBaseURI(), customerKey,
					contactIndex);

			// When : A valid connection is made

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.NOT_FOUND, "/contacts");
		}

	}

	static Stream<Arguments> parametersForPutInvalidRequest() {
		// @formatter:off
		//String customerKey, int contactIndex, String newDescription, String newEmail
		return Stream.of(
			//customer key
			arguments("*(&(*&*", "1", "New Email", "bobby@banking.net"),
			
			//description
			arguments("Bob", "1", "!@#$%^&*(", "bobby@banking.net"),
			
			//new email
			arguments("Bob", "1", "New Email", "bobbybanking.net"),
			arguments("Bob", "1", "New Email", "bobby@bankingnet"),
			arguments("Bob", "1", "New Email", "bobby@banking.n"),
			arguments("Bob", "1", "New Email", "bobby@banking.abcdefg")
		);
		// @formatter:on
	}

	static Stream<Arguments> parametersForPutUnknownRequest() {
		// @formatter:off
		//String customerKey, int contactIndex
		return Stream.of(
			arguments("Unknown Name", "1", "New Email", "bobby@banking.net"),
			arguments("Bob", "9", "New Email", "bobby@banking.net")
		);
		// @formatter:on
	}

	static Stream<Arguments> parametersForPostInvalidRequest() {
		// @formatter:off
		//String customerKey, int contactIndex, String description, String email
		return Stream.of(
			//customer name
			arguments("*(&(*&*", "2", "New Email", "bobby@banking.net"),
			
			//description
			arguments("Fred", "2", "!@#$%^&*(", "bobby@banking.net"),
			
			//new email
			arguments("Fred", "2", "New Email", "bobbybanking.net"),
			arguments("Fred", "2", "New Email", "bobby@bankingnet"),
			arguments("Fred", "2", "New Email", "bobby@banking.n"),
			arguments("Fred", "2", "New Email", "bobby@banking.abcdefg")
		);
		// @formatter:on
	}

	static Stream<Arguments> parametersForDeleteInvalidRequest() {
		// @formatter:off
		//String customerKey, int contactKey
		return Stream.of(
			//customer name
			arguments("*(&(*&*", 1),
			
			//new email
			arguments("C", 0)
		);
		// @formatter:on
	}

	static Stream<Arguments> parametersForPostUnknownRequest() {
		// @formatter:off
		//String customerName, String targetEmail
		return Stream.of(
			arguments("UnknownKey", 1),
			arguments("C", 2)
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
		ContactService contactService;

		@Test
		void testGetRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a get contacts request
			String uri = String.format("%s/contacts?customerKey=%s", getBaseURI(), "A");

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(contactService).fetchContactsByKey("A");

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/contacts");
		}

		@Test
		void testPutRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : an update contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"customerKey\": \"A\",\n"
					+ "    \"contact\": {\n"
					+ "        \"contactIndex\": 1,\n"
					+ "        \"description\": \"Old Email\",\n"
					+ "        \"email\": \"bobby@banking.net\"\n"
					+ "    }\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made but an error happens
			ContactPut errorBody = new ContactPut();
			errorBody.setCustomerKey("A");
			errorBody.setContact(
					Contact.builder().contactIndex(1).description("Old Email").email("bobby@banking.net").build());

			doThrow(new RuntimeException("Custom Error")).when(contactService).updateContact(errorBody);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.PUT, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/contacts");
		}

		@Test
		void testPostRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a create contact request with valid customer name

			// @formatter:off
			String body = "{\n"
					+ "    \"customerKey\" : \"B\",\n"
					+ "    \"contact\" : {\n"
					+ "        \"contactIndex\": 2,\n"
					+ "        \"description\" : \"Personal Email\",\n"
					+ "        \"email\" : \"fred@outlook.com\"\n"
					+ "    }\n"
					+ "}";
			// @formatter:on

			String uri = String.format("%s/contacts", getBaseURI());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);

			// When : A valid connection is made but an error happens
			ContactPost errorBody = new ContactPost();
			errorBody.setCustomerKey("B");
			errorBody.setContact(
					Contact.builder().contactIndex(2).description("Personal Email").email("fred@outlook.com").build());

			doThrow(new RuntimeException("Custom Error")).when(contactService).createContact(errorBody);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.POST, bodyEntity,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/contacts");
		}

		@Test
		void testDeleteRequestThatInteralErrorIsReturnedWhenAnErrorOccurs() {
			// Given : a delete contact request with valid parameters

			String uri = String.format("%s/contacts?customerKey=%s&contactIndex=%d", getBaseURI(), "C", 1);

			// When : A valid connection is made but an error occurs
			doThrow(new RuntimeException("Custom Error")).when(contactService).deleteContact("C", 1);

			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.DELETE, null,
					new ParameterizedTypeReference<>() {
					});

			// Then : assert that Bad request status is returned
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

			// And : the correct error message is returned
			Map<String, Object> error = response.getBody();
			assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR, "/contacts");
		}
	}

}
