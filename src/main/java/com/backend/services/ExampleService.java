package com.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.model.ExampleClass;
import com.backend.repositories.ExampleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExampleService {
	private final ExampleRepository exampleRepository;

	public List<ExampleClass> getAll() {
		return exampleRepository.findAll();
	}
}
