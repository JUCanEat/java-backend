package com.backend.controllers;

import com.backend.model.ExampleClass;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PythonClientController {

    @GetMapping("/call-python")
    @Operation(summary = "Call python", description = "Calls python at localhost:8000")
    public ResponseEntity<String> callPythonService() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8000";
        return restTemplate.getForEntity(url, String.class);
    }
}