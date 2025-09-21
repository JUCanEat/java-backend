package com.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.model.ExampleClass;
import com.backend.services.ExampleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

/** Example controller */
@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class ExampleController {
	private final ExampleService exampleService;

	@GetMapping("/")
	public String helloWorld() {
		return "Hello world";
	}

	@Operation(summary = "Get all examples", description = "Retrieves a list of all example objects")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of examples retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExampleClass.class))))})
	@GetMapping("/example")
	public List<ExampleClass> exampleEndpoint() {
		return exampleService.getAll();
	}
}
