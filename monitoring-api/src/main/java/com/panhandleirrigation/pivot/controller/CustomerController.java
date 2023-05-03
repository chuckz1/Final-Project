package com.panhandleirrigation.pivot.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.panhandleirrigation.pivot.entity.Customer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/pivots/customers")
@OpenAPIDefinition(info = @Info(title = "Pivot Monitoring API"), servers = {
		@Server(url = "http://localhost:8080", description = "Local Server.") })
public interface CustomerController {

	// @formatter:off
	
	@Operation(
			summary = "Returns a list of Customers", 
			description = "Returns a list of all Customers", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "A list of customers is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Customer.class))),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }
		)
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	List<Customer> fetchCustomers();
	
	
	@Operation(
			summary = "Updates an existing customer", 
			description = "Returns the updated customer", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "The updated customer is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Customer.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A customer was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "customer", 
					required = true,
					description = "The customer as JSON"),
			}
		)
	@PutMapping
	@ResponseStatus(code = HttpStatus.OK)
	Customer updateCustomer(@Valid @RequestBody Customer customer);
	
	// @formatter:on
}
