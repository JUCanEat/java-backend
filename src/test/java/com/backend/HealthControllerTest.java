package com.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.controllers.HealthController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
public class HealthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testHealthCheck() throws Exception {
		mockMvc.perform(get("/health")).andExpect(status().isOk()).andExpect(content().string("I am alive!"));
	}
}
