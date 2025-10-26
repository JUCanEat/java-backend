package com.backend.model.dtos;

import com.backend.model.entities.DailyMenu;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class DailyMenuDTO {
    private UUID id;
    private LocalDate date;
    private List<DishDTO> dishes;
    private String restaurantName;

    public DailyMenuDTO(DailyMenu dailyMenu) {
        this.id = dailyMenu.getId();
        this.date = dailyMenu.getDate();
        this.dishes = dailyMenu.getDishes().stream()
                .map(DishDTO::new)
                .collect(Collectors.toList());
        this.restaurantName = dailyMenu.getRestaurant().getName();
    }
}