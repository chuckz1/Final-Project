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

import com.panhandleirrigation.pivot.entity.Pivot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/pivots/pivots")
@OpenAPIDefinition(info = @Info(title = "Pivot Monitoring API"), servers = {
		@Server(url = "http://localhost:8080", description = "Local Server.") })
public interface PivotController {
	// @formatter:off
	
	@Operation(
			summary = "Returns a list of Pivots", 
			description = "Returns a list of all Pivots", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "A list of pivots is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Pivot.class))),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }
		)
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	List<Pivot> fetchPivots();
	
	
	@Operation(
			summary = "Updates an existing pivot", 
			description = "Returns the updated pivot", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "The updated pivot is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Pivot.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A pivot was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "pivot", 
					required = true,
					description = "The pivot to update as JSON"),
			}
		)
	@PutMapping
	@ResponseStatus(code = HttpStatus.OK)
	Pivot updatePivot(@Valid @RequestBody Pivot pivot);
	
	
	@Operation(
			summary = "Creates a new pivot", 
			description = "Returns the new pivot", 
			responses = {
				@ApiResponse(
					responseCode = "201", 
					description = "The new pivot is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = Pivot.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "pivot", 
					required = true,
					description = "The new pivot to create as JSON"),
			}
		)
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	Pivot createPivot(@Valid @RequestBody Pivot pivot);

	@Operation(
			summary = "Deletes a pivot", 
			description = "Deletes a pivot and returns nothing", 
			responses = {
				@ApiResponse(
					responseCode = "204", 
					description = "The pivot was deleted", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A pivot was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "pivotKey", 
					required = true,
					description = "The key of the pivot to delete")
			}
		)
	@DeleteMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deletePivot(
			@RequestParam
			@NotNull
			@Length(max = 10) 
			@Pattern(regexp = "[\\w\\s]*") 
			String pivotKey
		);
	
	// @formatter:on
}
