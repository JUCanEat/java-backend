package com.backend.services;

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
                Please analyze this menu image and extract all the items.
                
                Important rules:
                - Extract ALL menu items you can see
                - "category" must be EXACTLY either "SOUP" or "MAIN_COURSE" (uppercase)
                - "price" must be a BigNumber consisting of a number with two decimal places (e.g., 12.50)
                - "allergens" is an array that can contain ONLY these values: "NUTS", "GLUTEN", "MEAT", "LACTOSE"
                - Identify allergens based on the dish ingredients (e.g., beef = MEAT, cheese = LACTOSE, bread = GLUTEN)
                - If you're unsure about allergens, make an educated guess based on typical ingredients
                - If a dish clearly has no allergens from the list, use an empty array []
                - Change all Polish letters to its equivalents without diacritics (e.g., "ą" to "a", "ę" to "e")
                """;

        // main query to the LLM with image and prompt, automatically deserializing response to List<DishDTO>
        List<DishDTO> dishDTOs = chatClientBuilder.build()
                .prompt()
                .user(userSpec -> userSpec
                        .text(prompt)
                        .media(Media.builder()
                                .mimeType(MimeTypeUtils.IMAGE_JPEG)
                                .data(new ByteArrayResource(imageBytes))
                                .build()))
                .call()
                .entity(new ParameterizedTypeReference<List<DishDTO>>() {});

        // Convert DishDTOs to Dish entities and save them to the database
        List<Dish> savedDishes = new ArrayList<>();
        for (DishDTO dto : dishDTOs) {
            Dish dish = convertToEntity(dto);
            Dish savedDish = dishRepository.save(dish);
            savedDishes.add(savedDish);
        }
        return savedDishes;
    }

    // Helper method to convert DishDTO to Dish entity
    private Dish convertToEntity(DishDTO dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setCategory(Dish.Category.valueOf(dto.getCategory()));
        dish.setPrice(new Price(dto.getPrice(), "PLN"));
        dish.setAllergens(dto.getAllergens());
        return dish;
    }
}


