package com.backend.services;

public interface TranslationService {
    String translate(String text, String sourceLang, String targetLang);
}
