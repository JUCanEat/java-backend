package com.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeepLConfig {

    @Value("${deepl.api-key}")
    private String apiKey;

    @Value("${deepl.base-url:https://api-free.deepl.com/v2}")
    private String baseUrl;

    @Bean
    public WebClient deepLWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "DeepL-Auth-Key " + apiKey)
                .build();
    }
}