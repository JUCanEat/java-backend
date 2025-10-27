package com.backend.model.dtos;

import com.backend.model.entities.Location;
import com.backend.model.entities.Restaurant;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RestaurantListDTO {
    private UUID id;
    private String name;
    private String description;
    private String photoPath;
    private Location location;
    private boolean isOpenNow;

    public RestaurantListDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.photoPath = restaurant.getPhotoPath();
        this.location = restaurant.getLocation();
        this.isOpenNow = restaurant.isOpen();
    }
}