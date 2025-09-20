package com.backend.controllers;

import com.backend.model.ExampleClass;
import com.backend.services.ExampleService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Example controller
 */
@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService exampleService;
    @GetMapping("/")
    public String helloWorld(){
        return "Hello world";
    }

    @GetMapping("/example")
    public List<ExampleClass> exampleEndpoint(){
        return exampleService.getAll();
    }
}
