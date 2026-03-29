package com.backend.services;

import com.backend.model.DeepLResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

@Service
public class DeepLTranslationService implements TranslationService {
    private final WebClient webClient;

    public DeepLTranslationService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) {
            return text;
        }

        DeepLResponse response = webClient.post()
                .uri("/translate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("text", text)
                        .with("source_lang", sourceLang)
                        .with("target_lang", targetLang))
                .retrieve()
                .bodyToMono(DeepLResponse.class)
                .block();

        if (response == null || response.getTranslations() == null || response.getTranslations().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Translation failed");
        }

        return response.getTranslations().get(0).getText();
    }
}
