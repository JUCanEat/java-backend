package com.backend.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRestaurantRequest {
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String photoPath;
}