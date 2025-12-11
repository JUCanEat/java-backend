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
                You are a specialized menu digitization assistant. Analyze the provided image of a Polish handwritten restaurant menu and extract ALL visible menu items.
                                                                                                                
                EXTRACTION TASK:
                Read the handwritten menu carefully and identify every dish. For each dish, extract:
                - The dish name (exactly as written in Polish)
                - The category: determine if it's a soup or main course
                - The price in Polish złoty (format with 2 decimal places)
                - All applicable allergens based on the ingredients
                
                CATEGORY CLASSIFICATION:
                - Use "SOUP" for: zupy, rosół, barszcz, krem, chłodnik, zupa pomidorowa, bульон, and any other soup dishes
                - Use "MAIN_COURSE" for: all other dishes including meat dishes, fish, pasta, pierogi, kotlety, risotto, salads served as mains, casseroles, and vegetarian mains
                
                PRICE INTERPRETATION:
                - Look for prices next to each dish (usually marked with "zł" or just numbers)
                - Typical range: 8-50 zł for restaurant dishes
                - Always format with exactly 2 decimal places (12.50, 8.00, 25.90)
                - If no price is clearly visible, use 0.00
                
                ALLERGEN DETECTION:
                Identify allergens based on dish names and typical ingredients in Polish cuisine:
                
                MEAT: Any dish containing animal protein
                - Meats: kurczak, wołowina, wieprzowina, schabowy, kotlet, pieczeń, gulasz, kiełbasa, boczek, szynka
                - Fish/seafood: ryba, łosoś, dorsz, tuńczyk, krewetki, owoce morza
                
                GLUTEN: Dishes with wheat, bread, or pasta
                - Pasta/noodles: makaron, spaghetti, penne, lazania
                - Breaded: panierowany, w panierce
                - Dough-based: pierogi, kluski, kopytka, pyzy, naleśniki
                - Bread: pieczywo, grzanka, bułka
                
                LACTOSE: Dishes with dairy products
                - Cheese: ser, parmezan, mozzarella, gorgonzola, feta, oscypek
                - Cream/milk: śmietana, śmietankowy, mleko, kremowy, serowy
                - Butter: masło, maślany
                
                NUTS: Dishes with nuts
                - Orzechy, migdały, orzeszki, pistacje, laskowe
                
                EXAMPLES:
                - "Rosół z makaronem" → SOUP, MEAT + GLUTEN (chicken broth with noodles)
                - "Schabowy panierowany z ziemniakami" → MAIN_COURSE, MEAT + GLUTEN (breaded pork)
                - "Spaghetti carbonara" → MAIN_COURSE, GLUTEN + LACTOSE + MEAT
                - "Pierogi ruskie" → MAIN_COURSE, GLUTEN + LACTOSE (potato-cheese dumplings)
                - "Sałatka grecka" → MAIN_COURSE, LACTOSE (feta cheese)
                - "Zupa krem z brokułów" → SOUP, LACTOSE (cream-based)
                - "Grillowane warzywa" → MAIN_COURSE, [] (no allergens)
                
                Extract every menu item you can identify, even if the handwriting is challenging. Make your best interpretation of unclear text.
                """;

        // main query to the LLM with image and prompt, automatically deserializing response to List<DishDTO>
        List<AIDishDTO> AIdishDTOs = new ArrayList<AIDishDTO>();
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
        }catch(Exception e){
            log.error("Error when sending request to gpt api: {}", e.getMessage());
            return Collections.emptyList();
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


