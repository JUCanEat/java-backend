package com.backend.model.dtos;

import com.backend.model.entities.VendingMachine;
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
  //  private List<RestaurantListDTO> favouriteRestaurants;
//    private List<VendingMachine> favouriteVendingMachines;
    private List<RestaurantListDTO> ownedRestaurants;
}
