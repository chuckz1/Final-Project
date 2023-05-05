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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.panhandleirrigation.pivot.entity.Contact;
import com.panhandleirrigation.pivot.entity.ContactPost;
import com.panhandleirrigation.pivot.entity.ContactPut;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/pivots/contacts")
@OpenAPIDefinition(info = @Info(title = "Pivot Monitoring API"), servers = {
		@Server(url = "http://localhost:8080", description = "Local Server.") })
public interface ContactController {
	// @formatter:off
	
	@Operation(
			summary = "Returns a list of contacts", 
			description = "Returns a list of contacts based on customer key", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "A list of contacts is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Contact.class))),
				@ApiResponse(
						responseCode = "400", 
						description = "The request parameters are invalid", 
						content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A contact was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) },
			parameters = {
					@Parameter(
						name = "customerKey", 
						required = true,
						description = "The name of the customer to get contacts for")
			}
		)
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	List<Contact> fetchContactsByKey(@Valid @RequestParam @Length(max = 10) @Pattern(regexp = "[\\w\\s]*") String customerKey);
	
	
	@Operation(
			summary = "Updates an existing contact", 
			description = "Returns the updated contact", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "The updated contact is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Contact.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A contact was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "contactPut", 
					required = true,
					description = "The update contact request as JSON"),
			}
		)
	@PutMapping
	@ResponseStatus(code = HttpStatus.OK)
	Contact updateContact(@Valid @RequestBody ContactPut contactPut);
	
	
	@Operation(
			summary = "Creates a new contact", 
			description = "Returns the new contact", 
			responses = {
				@ApiResponse(
					responseCode = "201", 
					description = "The new contact is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Contact.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A contact was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "contactPost", 
					required = true,
					description = "The contact create request as JSON"),
				
			}
		)
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	Contact createContact(@Valid @RequestBody ContactPost contactPost);

	@Operation(
			summary = "Deletes a contact", 
			description = "Deletes a contact and returns nothing", 
			responses = {
				@ApiResponse(
					responseCode = "204", 
					description = "The contact was deleted", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A contact was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "customerKey", 
					required = true,
					description = "The customer key of the contact to delete"),
				@Parameter(
						name = "contactKey", 
						required = true,
						description = "The key of the contact to delete")
			}
		)
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deleteContact(
			@RequestParam
			@NotNull
			@Length(max = 10) 
			@Pattern(regexp = "[\\w\\s]*") 
			String customerKey, 
			@RequestParam
			@NotNull
			@Length(max = 10) 
			@Pattern(regexp = "[\\w\\s]*") 
			String contactKey
		);
		
	// @formatter:on
}
