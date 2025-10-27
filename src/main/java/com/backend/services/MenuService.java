package com.backend.services;

import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.DailyMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {
    private final DailyMenuRepository dailyMenuRepository;
    public DailyMenuDTO getDailyMenuByRestaurantId(UUID id) { //TO DO : ADD UNIQUE CONSTRAINT IN DB ON DAILY MENU + ACTIVE
        Optional<DailyMenu> menu = dailyMenuRepository
                .findByRestaurantIdAndStatus(id, DailyMenu.Status.ACTIVE);

        return menu.map(DailyMenuDTO::new)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Daily menu not found for restaurant: " + id
                ));
    }
}
