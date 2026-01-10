package com.backend.services;

import com.backend.config.RabbitMQConfig;
import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.MessageDTO;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.DishRepository;
import com.backend.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final DailyMenuRepository dailyMenuRepository;
    private final RestaurantRepository restaurantRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DishRepository dishRepository;
    //private final MenuAIService menuAIService; //Musisz to alek napisać

    public DailyMenuDTO getDailyMenuByRestaurantId(UUID id) { //TO DO : ADD UNIQUE CONSTRAINT IN DB ON DAILY MENU + ACTIVE
        Optional<DailyMenu> menu = dailyMenuRepository
                .findByRestaurantIdAndStatus(id, DailyMenu.Status.ACTIVE);

        return menu.map(DailyMenuDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Daily menu not found for restaurant: " + id
                ));
    }

    @Transactional
    public void uploadMenuImage(
            UUID restaurantId,
            MultipartFile image,
            String ownerId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (restaurant.getOwners().stream().noneMatch(owner -> owner.getId().equals(ownerId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the owner of this restaurant");
        }

        LocalDate today = LocalDate.now();

        DailyMenu menu = new DailyMenu();
        menu.setRestaurant(restaurant);
        menu.setDate(today);
        menu.setStatus(DailyMenu.Status.PROCESSING);
        menu.setOriginalFileName(image.getOriginalFilename());

        DailyMenu saved = dailyMenuRepository.save(menu);
        byte[] imageBytes;
        try {
            imageBytes= image.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error reading file: " + e.getMessage());
        }

        MessageDTO message = new MessageDTO(
                saved.getId(),
                restaurantId,
                today,
                imageBytes,
                image.getOriginalFilename(),
                ownerId
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MENU_PROCESSING_EXCHANGE,
                RabbitMQConfig.MENU_PROCESSING_ROUTING_KEY,
                message
        );

        log.info("Menu processing message sent for menuId {}", saved.getId());
    }

    public DailyMenuDTO getDailyMenuDraftByRestaurantId(UUID id) {
        Optional<DailyMenu> menu = dailyMenuRepository
                .findByRestaurantIdAndStatus(id, DailyMenu.Status.DRAFT);

        return menu.map(DailyMenuDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Daily menu draft not found for restaurant: " + id
                ));
    }
    @Transactional
    public void updateAndApproveMenu(UUID restaurantId, DailyMenuDTO request, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant not found"));


        if (restaurant.getOwners().stream().noneMatch(owner -> owner.getId().equals(ownerId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the owner of this restaurant");
        }

        Optional<DailyMenu> processingMenu = dailyMenuRepository
                .findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.PROCESSING);

        if (processingMenu.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Menu is currently being processed by OCR. Please wait for processing to complete.");
        }

        Optional<DailyMenu> existingMenu = dailyMenuRepository
                .findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.DRAFT);

        DailyMenu menu;

        if (existingMenu.isPresent()) {
            menu = existingMenu.get();
            menu.getDishes().clear();
        } else {
            menu = new DailyMenu();
            menu.setRestaurant(restaurant);
            menu.setDate(LocalDate.now());
        }

        if (request.getDishes() != null && !request.getDishes().isEmpty()) {
            menu.getDishes().clear();

            List<Dish> updatedDishes = request.getDishes().stream()
                    .map(dto -> {
                        Dish dish = new Dish();
                        dish.setCategory(Dish.Category.valueOf(dto.getCategory()));
                        dish.setName(dto.getName());
                        dish.setPrice(new Price(dto.getPrice(), "PLN"));
                        dish.setAllergens(dto.getAllergens());
                        dishRepository.save(dish);
                        //aiservice.index(dish)
                        return dish;
                    })
                    .collect(Collectors.toList());

            menu.getDishes().addAll(updatedDishes);
        }

        Optional<DailyMenu> previousMenu = dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE);
        if (previousMenu.isPresent()) {
            previousMenu.get().setStatus(DailyMenu.Status.INACTIVE);
            dailyMenuRepository.save(previousMenu.get());
        }

        menu.setStatus(DailyMenu.Status.ACTIVE);
        dailyMenuRepository.save(menu);
    }
}
