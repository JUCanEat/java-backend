package com.backend.services;

import com.backend.model.entities.Dish;
import com.backend.model.entities.DishTranslation;
import com.backend.repositories.DishTranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DishTranslationService {
    private final DishTranslationRepository dishTranslationRepository;
    private final TranslationService translationService;

    public void upsertBothLanguages(Dish dish, String sourceName, String sourceLanguageHeader) {
        DishTranslation.Language sourceLanguage = parseLanguage(sourceLanguageHeader);
        DishTranslation.Language targetLanguage = sourceLanguage == DishTranslation.Language.PL
                ? DishTranslation.Language.EN
                : DishTranslation.Language.PL;

        upsertTranslation(dish, sourceLanguage, sourceName);

        String translatedName;
        try {
            translatedName = translationService.translate(sourceName, sourceLanguage.name(), targetLanguage.name());
        } catch (Exception ex) {
            log.warn("Translation failed for dish {} from {} to {}. Falling back to source text.",
                    dish.getId(), sourceLanguage, targetLanguage, ex);
            translatedName = sourceName;
        }

        upsertTranslation(dish, targetLanguage, translatedName);
    }

    public Map<UUID, String> getDishNamesByLanguage(List<UUID> dishIds, String languageHeader) {
        DishTranslation.Language language = parseLanguage(languageHeader);
        return dishTranslationRepository.findByDishIdInAndLanguage(dishIds, language).stream()
                .collect(Collectors.toMap(translation -> translation.getDish().getId(), DishTranslation::getName));
    }

    public String resolveLanguageCode(String languageHeader) {
        return parseLanguage(languageHeader).name();
    }

    private void upsertTranslation(Dish dish, DishTranslation.Language language, String name) {
        DishTranslation translation = dishTranslationRepository.findByDishIdAndLanguage(dish.getId(), language)
                .orElseGet(DishTranslation::new);

        translation.setDish(dish);
        translation.setLanguage(language);
        translation.setName(name);

        dishTranslationRepository.save(translation);
    }

    private DishTranslation.Language parseLanguage(String header) {
        if (header == null || header.isBlank()) {
            return DishTranslation.Language.PL;
        }

        String normalized = header.toLowerCase();
        if (normalized.startsWith("en")) {
            return DishTranslation.Language.EN;
        }
        return DishTranslation.Language.PL;
    }
}
