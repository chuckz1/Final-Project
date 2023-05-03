package com.panhandleirrigation.pivot.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

public class BaseTestSupport {

	@Autowired
	protected TestRestTemplate restTemplate;

	@LocalServerPort
	protected int serverPort;

	protected String getBaseURI() {
		return String.format("http://localhost:%d/pivots", serverPort);
	}
	
	protected void assertErrorMessageValid(Map<String, Object> error, HttpStatus status, String uri) {
		// @formatter:off
		assertThat(error)
			.containsKey("message")
			.containsEntry("status code", status.value())
			.containsEntry("uri", "/pivots" + uri)
			.containsKey("timestamp")
			.containsEntry("reason", status.getReasonPhrase());
		// @formatter:on	
	}

}
