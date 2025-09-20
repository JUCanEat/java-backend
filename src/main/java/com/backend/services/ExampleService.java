package com.backend.services;

import com.backend.model.ExampleClass;
import com.backend.repositories.ExampleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExampleService {
    private final ExampleRepository exampleRepository;
    public List<ExampleClass> getAll(){
        return exampleRepository.findAll();
    }
}
