package com.backend.model;

import com.backend.config.RabbitMQConfig;
import com.backend.model.dtos.MessageDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.DishRepository;
import com.backend.services.MenuAIService;
import com.backend.services.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuProcessingListener {
    private final DailyMenuRepository dailyMenuRepository;
    private final DishRepository dishRepository;
    // private final ElasticsearchService elasticsearchService; // opcjonalnie
    private final SseEmitterService sseEmitterService;
    private final MenuAIService menuAIService;

    @RabbitListener(queues = RabbitMQConfig.MENU_PROCESSING_QUEUE)
    @Transactional
    public void processMenu(MessageDTO message) {
        log.info("Processing menu: {}", message.getId());

        try {
            DailyMenu menu = dailyMenuRepository.findById(message.getId())
                    .orElseThrow(() -> new RuntimeException("Menu not found: " + message.getId()));

            // Call AI service to parse menu from image
            List<Dish> parsedItems = menuAIService.parseMenuFromImage(message.getImageData());

            log.info("Successfully processed image {} with {} items", message.getFileName(), parsedItems.size());
            if (parsedItems.isEmpty()) {
                throw new RuntimeException("No items parsed from image");
            }

            menu.setDishes(parsedItems);
            menu.setStatus(DailyMenu.Status.DRAFT);
            //menu.setProcessedAt(LocalDateTime.now()); do audit logów?

            dailyMenuRepository.save(menu);

            //EVENT DLA FRONTENDU!!!
            sseEmitterService.sendEvent(message.getOwnerId(), menu.getId());


        } catch (Exception e) {
            log.error("Failed to process menu: {} : {}", message.getId(), e.getMessage());

            dailyMenuRepository.findById(message.getId()).ifPresent(menu -> {
                menu.setStatus(DailyMenu.Status.FAILED);
                dailyMenuRepository.save(menu);
            });

        }
    }
}