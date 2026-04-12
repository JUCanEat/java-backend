package com.backend.controllers;

import com.backend.services.TranslationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translate")
public class TranslationController {
    private final TranslationService service;

    public TranslationController(TranslationService service) {
        this.service = service;
    }

    @GetMapping("")
    public String translate(
            @RequestParam String text,
            @RequestParam(defaultValue = "PL") String sourceLang,
            @RequestParam(defaultValue = "EN") String targetLang) {
        return service.translate(text, sourceLang, targetLang);
    }
}
