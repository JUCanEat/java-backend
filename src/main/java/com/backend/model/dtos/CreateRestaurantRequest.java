package com.backend.model.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateRestaurantRequest {
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String photoPath;
    private List<OpeningHoursDTO> openingHours;
}