package com.backend.services;

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
import org.springframework.beans.factory.annotation.Value;
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
    private final ObjectMapper objectMapper;
    private final DishRepository dishRepository;

    public String chat(String prompt) {
        return chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    public List<Dish> parseMenuFromImage(byte[] imageBytes) throws JsonProcessingException {
        String prompt = """
                Please analyze this menu image and extract all the items in JSON format.
                
                Return ONLY a valid JSON object with this structure:
                {
                    {
                      "name": "Item name",
                      "category": "SOUP or MAIN_COURSE",
                      "price": 10.99,
                      "allergens": ["NUTS", "GLUTEN"]
                    }
                  ]
                }
                
                Important rules:
                - Extract ALL menu items you can see
                - "category" must be EXACTLY either "SOUP" or "MAIN_COURSE" (uppercase)
                - "price" must be a string consisting of a number with two decimal places (e.g., 12.50) and a PLN currency
                - "allergens" is an array that can contain ONLY these values: "NUTS", "GLUTEN", "MEAT", "LACTOSE"
                - Identify allergens based on the dish ingredients (e.g., beef = MEAT, cheese = LACTOSE, bread = GLUTEN)
                - If you're unsure about allergens, make an educated guess based on typical ingredients
                - If a dish clearly has no allergens from the list, use an empty array []
                - Change all Polish letters to its equivalents without diacritics (e.g., "ą" to "a", "ę" to "e")
                """;

        String response = chatClientBuilder.build()
                .prompt()
                .user(userSpec -> userSpec
                        .text(prompt)
                        .media(Media.builder()
                                .mimeType(MimeTypeUtils.IMAGE_JPEG)
                                .data(new ByteArrayResource(imageBytes))
                                .build()))
                .call()
                .content();

        System.out.println("GPT MODEL RESPONSE:");
        System.out.println(response);

        return parseJsonResponse(response);
    }


    private List<Dish> parseJsonResponse(String response) throws JsonProcessingException {
        String cleanJson = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
        return objectMapper.readValue(cleanJson, new TypeReference<List<Dish>>(){});
    }
}


