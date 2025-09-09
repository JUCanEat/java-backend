package com.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Example controller
 */
@RestController
@RequestMapping("/hello")
public class ExampleController {
    @GetMapping("/")
    public String helloWorld(){
        return "Hello world";
    }
}
