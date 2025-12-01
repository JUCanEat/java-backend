package com.backend.model.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private List<FacilityDTO> favourites;
    private List<RestaurantListDTO> ownedRestaurants;
}
