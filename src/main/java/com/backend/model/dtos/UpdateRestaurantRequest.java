package com.backend.model.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRestaurantRequest {
    private String name;
    private String description;
    private String photoPath;
    private Double latitude;
    private Double longitude;
    private List<OpeningHoursDTO> openingHours;
}