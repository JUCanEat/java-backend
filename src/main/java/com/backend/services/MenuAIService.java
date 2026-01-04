package com.backend.services;

import com.backend.model.dtos.AIDishDTO;
import com.backend.model.dtos.DishDTO;
import com.backend.model.entities.Dish;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DishRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MenuAIService {
    private final ChatClient.Builder chatClientBuilder;
    private final DishRepository dishRepository;


    public List<Dish> parseMenuFromImage(byte[] imageBytes){
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
        int maxRetries = 5;
        List<AIDishDTO> AIdishDTOs = new ArrayList<>();
        // main query to the LLM with image and prompt, automatically deserializing response to List<DishDTO>
        for(int i = 0; i < 5; i++) {
            System.out.println("sending menu to AI - attempt" + i + "/" + maxRetries);

            try {
                AIdishDTOs = chatClientBuilder.build()
                        .prompt()
                        .user(userSpec -> userSpec
                                .text(prompt)
                                .media(Media.builder()
                                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                                        .data(new ByteArrayResource(imageBytes))
                                        .build()))
                        .call()
                        .entity(new ParameterizedTypeReference<List<AIDishDTO>>() {
                        });
            } catch (Exception e) {
                System.out.println("gpt api request failed try" + i + "/" + maxRetries);
                log.error("Error when sending request to gpt api: {}", e.getMessage());
                continue;
            }
            if(i < maxRetries-1 && !AIdishDTOs.isEmpty()) {
                System.out.println("successfully got valid response from gpt api");
                break;
            } else {
                log.error("failed to get valid response from gpt api after " + maxRetries + " attempts, returning empty menu. If no errors returned before, there was probably no items visible in the photo");
                return new ArrayList<Dish>();
            }
        }
        // Convert DishDTOs to Dish entities and save them to the database
        List<Dish> savedDishes = new ArrayList<>();
        for (AIDishDTO dto : AIdishDTOs) {
            Dish dish = convertToEntity(dto);
            Dish savedDish = dishRepository.save(dish);
            savedDishes.add(savedDish);
        }
        return savedDishes;
    }

    // Helper method to convert DishDTO to Dish entity
    private Dish convertToEntity(AIDishDTO dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setCategory(Dish.Category.valueOf(dto.getCategory()));
        dish.setPrice(new Price(dto.getPrice(), "PLN"));
        dish.setAllergens(dto.getAllergens());
        return dish;
    }
}


