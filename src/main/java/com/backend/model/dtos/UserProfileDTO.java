package com.backend.model.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Getter @Setter
public class UserProfileDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private List<FacilityDTO> favourites;
    private List<RestaurantListDTO> ownedRestaurants;
}
