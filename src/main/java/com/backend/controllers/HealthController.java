package com.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("I am alive!");
	}

	@GetMapping("/whoami")
	public Map<String, Object> whoAmI(Authentication auth) {
		return Map.of(
				"name", auth.getName(),
				"authorities", auth.getAuthorities()
		);
	}
}

