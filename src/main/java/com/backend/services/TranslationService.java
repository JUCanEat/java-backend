package com.backend.services;

import org.springframework.web.reactive.function.client.WebClient;

public class TranslateService {
    private final WebClient webClient;

    public TranslateService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String check() {
        return "abc";

    }}
