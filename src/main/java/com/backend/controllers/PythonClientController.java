package com.backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class PythonClientController {

	@Value("${microservice.url}")
	private String microserviceUrl;

	@GetMapping("/call-python")
	@Operation(summary = "Call python", description = "Calls python at localhost:8000")
	public ResponseEntity<String> callPythonService() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForEntity(microserviceUrl, String.class);
	}
}
