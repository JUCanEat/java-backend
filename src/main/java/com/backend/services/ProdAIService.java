package com.backend.services;

import com.backend.model.dtos.AIDishDTO;
import com.backend.model.entities.Dish;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.content.Media;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"prod"})
public class ProdAIService implements MenuAIService {

    private final ChatClient.Builder chatClientBuilder;
    private final DishRepository dishRepository;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)  // 1s, 2s, 4s, 8s
    )
    @Override
    public List<Dish> parseMenuFromImage(byte[] imageBytes) {
        int attempt = RetrySynchronizationManager.getContext().getRetryCount() + 1;
        log.info("Sending menu to AI - attempt {}/5", attempt);

        String prompt = """
            You are a specialized menu digitization assistant for Polish restaurants. Analyze the provided image of a handwritten menu and extract ALL visible items.
            
            CRITICAL: Your response MUST be a valid JSON array. Do not include any explanatory text, markdown formatting, or code blocks.
            
            EXTRACTION REQUIREMENTS:
            For each dish, extract:
            1. name: The exact dish name in Polish (string, required)
            2. category: Either "SOUP" or "MAIN_COURSE" (string, required)
            3. price: Numeric value with 2 decimal places (number, required)
            4. allergens: Array of allergen codes (array, can be empty)
            
            CATEGORY RULES (choose one):
            - "SOUP": zupy, rosół, barszcz, krem, chłodnik, zupa, bulion, consommé
            - "MAIN_COURSE": everything else (mains, sides, salads, desserts, appetizers)
            
            PRICE HANDLING:
            - Extract numbers near dish names (usually 8-50 zł range)
            - Format: always use decimal number (e.g., 12.50, 8.00, 25.90)
            - If price is unclear or missing: use 0.00
            - Remove any currency symbols (zł, PLN)
            
            ALLERGEN CODES (use exact strings):
            - "MEAT": kurczak, wołowina, wieprzowina, schabowy, kotlet, ryba, łosoś, krewetki, etc.
            - "GLUTEN": makaron, spaghetti, pierogi, kluski, panierowany, w panierce, pieczywo
            - "LACTOSE": ser, śmietana, kremowy, mleko, masło, parmezan, mozzarella
            - "NUTS": orzechy, migdały, orzeszki, pistacje
            
            Use empty array [] if no allergens apply.
            
            EXAMPLES OF EXPECTED OUTPUT:
            [
              {"name": "Rosół z makaronem", "category": "SOUP", "price": 12.00, "allergens": ["MEAT", "GLUTEN"]},
              {"name": "Schabowy panierowany", "category": "MAIN_COURSE", "price": 28.50, "allergens": ["MEAT", "GLUTEN"]},
              {"name": "Sałatka grecka", "category": "MAIN_COURSE", "price": 18.00, "allergens": ["LACTOSE"]},
              {"name": "Grillowane warzywa", "category": "MAIN_COURSE", "price": 15.00, "allergens": []}
            ]
            
            IMPORTANT RULES:
            - Extract EVERY visible dish, even if handwriting is unclear
            - Make best-effort interpretation of unclear text
            - Do NOT invent dishes that aren't visible
            - Do NOT include menu headers, restaurant names, or non-dish text
            - Ensure ALL fields are present for each dish
            - Response must be parseable JSON - no additional text
            
            Return only the JSON array of dishes.
            """;

        List<AIDishDTO> aiDishDTOs = chatClientBuilder.build()  // ← .build() from newer version
                .prompt()
                .user(userSpec -> userSpec
                        .text(prompt)
                        .media(Media.builder()
                                .mimeType(MimeTypeUtils.IMAGE_JPEG)
                                .data(new ByteArrayResource(imageBytes))
                                .build()))
                .call()
                .entity(new ParameterizedTypeReference<List<AIDishDTO>>() {});



        List<Dish> savedDishes = new ArrayList<>();
        for (AIDishDTO dto : aiDishDTOs) {
            savedDishes.add(dishRepository.save(convertToEntity(dto)));
        }
        if (savedDishes.isEmpty()) {
            throw new IllegalStateException("Empty response from GPT - no dishes extracted");
        }

        log.info("Successfully parsed and saved {} dishes from image", savedDishes.size());
        return savedDishes;
    }

    @Recover
    public List<Dish> recover(Exception e, byte[] imageBytes) {
        log.error("All GPT retry attempts exhausted: {}", e.getMessage());
        return Collections.emptyList();
    }

    private Dish convertToEntity(AIDishDTO dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setCategory(Dish.Category.valueOf(dto.getCategory()));
        dish.setPrice(new Price(dto.getPrice(), "PLN"));
        dish.setAllergens(dto.getAllergens());
        return dish;
    }
}