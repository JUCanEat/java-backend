package com.backend.model.dtos;

import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Location;
import com.backend.model.entities.OpeningHours;
import com.backend.model.entities.Restaurant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class RestaurantDetailsDTO {
    private UUID id;
    private String name;
    private String description;
    private String photoPath;
    private Location location;
    private List<OpeningHoursDTO> openingHours;
    private DailyMenuDTO todayMenu;
    private int favoritesCount; //TO DO?

    public RestaurantDetailsDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.photoPath = restaurant.getPhotoPath();
        this.location = restaurant.getLocation();
        this.openingHours = restaurant.getOpeningHours()
                .stream()
                .map(OpeningHoursDTO::new)
                .collect(Collectors.toList());
        this.todayMenu = new DailyMenuDTO(restaurant.getTodayMenu());
    }

}