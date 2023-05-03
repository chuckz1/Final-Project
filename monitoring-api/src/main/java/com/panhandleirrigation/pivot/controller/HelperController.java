package com.panhandleirrigation.pivot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Validated
@RequestMapping("/pivots/helper")
@OpenAPIDefinition(info = @Info(title = "Pivot Monitoring API"), servers = {
		@Server(url = "http://localhost:8080", description = "Local Server.") })
public interface HelperController {

	@Operation(
			summary = "Generates Key", 
			description = "Returns a new key that is check against the provided table&column", 
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "The key is returned", 
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = String.class))),
				@ApiResponse(
					responseCode = "400", 
					description = "The request parameters are invalid", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "404", 
					description = "A table or column was not found with the input criteria", 
					content = @Content(mediaType = "application/json")),
				@ApiResponse(
					responseCode = "500", 
					description = "An unplanned error occured", 
					content = @Content(mediaType = "application/json")) }, 
			parameters = {
				@Parameter(
					name = "table", 
					required = true,
					description = "The table to check the key against"),
				@Parameter(
						name = "column", 
						required = false,
						description = "The column to check the key against")
			}
		)
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	String generateKey(@RequestParam String table, @RequestParam String column);
}
