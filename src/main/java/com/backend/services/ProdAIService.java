    package com.backend.services;

    import com.backend.model.dtos.AIDishDTO;
    import com.backend.model.entities.Dish;
    import com.backend.model.valueObjects.Price;
    import com.backend.repositories.DishRepository;
    import lombok.extern.slf4j.Slf4j;

    import org.springframework.ai.chat.client.ChatClient;
    import org.springframework.ai.content.Media;
    import org.springframework.context.annotation.Profile;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.core.io.ByteArrayResource;
    import org.springframework.retry.RetryContext;
    import org.springframework.retry.annotation.Backoff;
    import org.springframework.retry.annotation.Recover;
    import org.springframework.retry.annotation.Retryable;
    import org.springframework.retry.support.RetrySynchronizationManager;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.util.MimeType;
    import org.springframework.util.MimeTypeUtils;
    import org.springframework.web.client.HttpServerErrorException;
    import org.springframework.web.client.ResourceAccessException;

    import java.util.List;

    @Service
    @Slf4j
    @Profile({"!dev"})
    public class ProdAIService implements MenuAIService {

        private final ChatClient chatClient;
        private final DishRepository dishRepository;

        public ProdAIService(ChatClient.Builder chatClientBuilder, DishRepository dishRepository) {
            this.chatClient = chatClientBuilder.build();
            this.dishRepository = dishRepository;
        }

        /**
         * Retries ONLY on transient infrastructure failures (network blips, 5xx).
         * Deterministic failures (empty/malformed AI response, bad input) propagate
         * straight to {@link #recover} without burning more API calls.
         */
        @Retryable(
                retryFor = {
                        ResourceAccessException.class,
                        HttpServerErrorException.class
                },
                noRetryFor = {
                        IllegalArgumentException.class,
                        MenuExtractionException.class
                },
                maxAttempts = 5,
                backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
        )

        @Override
        public List<Dish> parseMenuFromImage(byte[] imageBytes) {
            RetryContext ctx = RetrySynchronizationManager.getContext();
            int attempt = (ctx == null ? 0 : ctx.getRetryCount()) + 1;
            if (ctx != null && ctx.getLastThrowable() != null) {
                log.warn("Sending menu to AI - attempt {} (previous failure: {})",
                        attempt, ctx.getLastThrowable().toString());
            } else {
                log.info("Sending menu to AI - attempt {}", attempt);
            }

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

            MimeType mimeType = detectImageMimeType(imageBytes);

            List<AIDishDTO> aiDishDTOs = chatClient
                    .prompt()
                    .user(userSpec -> userSpec
                            .text(prompt)
                            .media(Media.builder()
                                    .mimeType(mimeType)
                                    .data(new ByteArrayResource(imageBytes))
                                    .build()))
                    .call()
                    .entity(new ParameterizedTypeReference<List<AIDishDTO>>() {});

            if (aiDishDTOs == null || aiDishDTOs.isEmpty()) {
                // Deterministic: same image will produce same empty result. Do NOT retry.
                throw new MenuExtractionException(
                        "AI returned no dishes - image is likely not a readable menu");
            }

            List<Dish> entities = aiDishDTOs.stream()
                    .map(this::convertToEntity)
                    .toList();
            List<Dish> savedDishes = dishRepository.saveAll(entities);

            log.info("Successfully parsed and saved {} dishes from image", savedDishes.size());
            return savedDishes;
        }

        @Recover
        public List<Dish> recover(Exception e, byte[] imageBytes) {
            log.error("Menu parsing failed - giving up", e);
            throw new MenuExtractionException("Failed to parse menu: " + e.getMessage(), e);
        }

        private Dish convertToEntity(AIDishDTO dto) {
            Dish dish = new Dish();
            dish.setName(dto.getName());
            dish.setCategory(parseCategory(dto.getCategory()));
            dish.setPrice(new Price(dto.getPrice(), "PLN"));
            dish.setAllergens(dto.getAllergens());
            return dish;
        }

        private Dish.Category parseCategory(String raw) {
            if (raw == null) {
                return Dish.Category.MAIN_COURSE;
            }
            return switch (raw.trim().toUpperCase()) {
                case "SOUP" -> Dish.Category.SOUP;
                default -> Dish.Category.MAIN_COURSE;
            };
        }

        private MimeType detectImageMimeType(byte[] bytes) {
            if (bytes != null && bytes.length >= 4
                    && (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 'P'
                    && bytes[2] == 'N'
                    && bytes[3] == 'G') {
                return MimeTypeUtils.IMAGE_PNG;
            }
            return MimeTypeUtils.IMAGE_JPEG;
        }

        /** Thrown when the AI cannot extract a menu from the given image. Not retryable. */
        public static class MenuExtractionException extends RuntimeException {
            public MenuExtractionException(String message) {
                super(message);
            }
            public MenuExtractionException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }