package com.panhandleirrigation.pivot.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.CustomerPivot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/pivots/customer_pivots")
@OpenAPIDefinition(info = @Info(title = "Pivot Monitoring API"), servers = {
		@Server(url = "http://localhost:8080", description = "Local Server.") })
public interface CustomerPivotController {
	// @formatter:off
	
	@Operation(
			summary = "Returns a list of relationships", 
			description = "Returns a list of relationships between a customer and their pivots", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "A list of relationships is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Contact.class))),
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
						name = "customerKey", 
						required = true,
						description = "The name of the customer to get pivots for")
			}
		)
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	List<CustomerPivot> fetchCustomerPivotsByKey(@Valid @RequestParam @Length(max = 10) @Pattern(regexp = "[\\w\\s]*") String customerKey);
	
	@Operation(
			summary = "Creates a new relationship", 
			description = "Returns the new relationship", 
			responses = {
				@ApiResponse(
					responseCode = "201", 
					description = "The new relationship is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = CustomerPivot.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A customer/pivot was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "customerPivot", 
					required = true,
					description = "The relationship to create as JSON"),
				
			}
		)
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	CustomerPivot createCustomerPivot(@Valid @RequestBody CustomerPivot customerPivot);

	@Operation(
			summary = "Deletes a relationship", 
			description = "Deletes a relationship and returns nothing", 
			responses = {
				@ApiResponse(
					responseCode = "204", 
					description = "The relationship was deleted", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A relationship was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "customerPivotKey", 
					required = true,
					description = "The public key of the relationship to delete")
			}
		)
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deleteCustomerPivot(
			@RequestParam
			@NotNull
			@Length(max = 10) 
			@Pattern(regexp = "[\\w\\s]*") 
			String customerPivotKey
		);
		
	// @formatter:on
}
