package com.backend.services;

import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.RestaurantRepository;
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
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public List<RestaurantListDTO> getAllRestaurants(){
        return restaurantRepository.findAll().stream().map(RestaurantListDTO::new).collect(Collectors.toList());
    }

    public RestaurantDetailsDTO getRestaurantById(UUID id){
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
        }
        return new RestaurantDetailsDTO(restaurant.get());
    }
}
