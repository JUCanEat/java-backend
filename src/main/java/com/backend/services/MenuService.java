package com.backend.services;

import com.backend.config.RabbitMQConfig;
import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.MessageDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.DishRepository;
import com.backend.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuService {
    private final DailyMenuRepository dailyMenuRepository;
    private final RestaurantRepository restaurantRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DishRepository dishRepository;
    //private final MenuAIService menuAIService; //Musisz to alek napisać

    public DailyMenuDTO getDailyMenuByRestaurantId(UUID id) { //TO DO : ADD UNIQUE CONSTRAINT IN DB ON DAILY MENU + ACTIVE
        LocalDate today = LocalDate.now();
        Optional<DailyMenu> menu = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(id, DailyMenu.Status.ACTIVE, today);

        return menu.map(DailyMenuDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Daily menu not found for restaurant: " + id + " and date: " + today
                ));
    }

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

        // check if there is existing processed menu;
        // no check against PROCESSING, deleting it would not prevent the in-memory
        // one from being saved as draft - and then the draft would be our demise.

        // todo: test by asserting deletion was called
        dailyMenuRepository.deleteByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, today);

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

    public void updateAndApproveMenu(UUID restaurantId, DailyMenuDTO request, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Restaurant not found"));


        if (restaurant.getOwners().stream().noneMatch(owner -> owner.getId().equals(ownerId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the owner of this restaurant");
        }

        if (request.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Menu date is required");
        }

        LocalDate targetDate = request.getDate();
        if (targetDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot publish or schedule a menu in the past");
        }

        DailyMenu.Status targetStatus = targetDate.isAfter(LocalDate.now())
                ? DailyMenu.Status.SCHEDULED
                : DailyMenu.Status.ACTIVE;

        Optional<DailyMenu> processingMenu = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, targetDate);

        if (processingMenu.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Menu is currently being processed by OCR. Please wait for processing to complete.");
        }

        DailyMenu menu = resolveMenuToUpdate(restaurantId, request, targetDate, restaurant);
        menu.setDate(targetDate);

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

        Optional<DailyMenu> previousMenuForDate = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(restaurantId, targetStatus, targetDate);
        if (previousMenuForDate.isPresent() && (menu.getId() == null || !previousMenuForDate.get().getId().equals(menu.getId()))) {
            previousMenuForDate.get().setStatus(DailyMenu.Status.INACTIVE);
            dailyMenuRepository.save(previousMenuForDate.get());
        }

        menu.setStatus(targetStatus);
        dailyMenuRepository.save(menu);
    }

    private DailyMenu resolveMenuToUpdate(UUID restaurantId, DailyMenuDTO request, LocalDate targetDate, Restaurant restaurant) {
        if (request.getId() != null) {
            DailyMenu menuById = dailyMenuRepository.findByIdAndRestaurantId(request.getId(), restaurantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Menu not found for provided id"));

            if (menuById.getStatus() != DailyMenu.Status.DRAFT
                    && menuById.getStatus() != DailyMenu.Status.ACTIVE
                    && menuById.getStatus() != DailyMenu.Status.SCHEDULED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Only draft, published or scheduled menus can be edited");
            }

            return menuById;
        }

        Optional<DailyMenu> draftForDate = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, targetDate);

        if (draftForDate.isPresent()) {
            return draftForDate.get();
        }

        Optional<DailyMenu> scheduledForDate = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.SCHEDULED, targetDate);

        if (scheduledForDate.isPresent()) {
            return scheduledForDate.get();
        }

        Optional<DailyMenu> activeForDate = dailyMenuRepository
                .findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, targetDate);

        if (activeForDate.isPresent()) {
            return activeForDate.get();
        }

        DailyMenu menu = new DailyMenu();
        menu.setRestaurant(restaurant);
        menu.setDate(targetDate);
        return menu;
    }
}
