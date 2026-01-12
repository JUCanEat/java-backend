package com.backend.services;

import com.backend.model.dtos.CreateRestaurantRequest;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.entities.Location;
import com.backend.model.entities.OpeningHours;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.model.valueObjects.Latitude;
import com.backend.model.valueObjects.Longitude;
import com.backend.repositories.RestaurantRepository;
import com.backend.repositories.UserRepository;
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
    private final UserRepository userRepository;

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
    @Transactional
    public RestaurantDetailsDTO createRestaurant(CreateRestaurantRequest request, String userId) {
        System.out.println("In service.");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Location location = new Location();
        location.setLatitude(new Latitude(request.getLatitude()));
        location.setLongitude(new Longitude(request.getLongitude()));

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setLocation(location);
        restaurant.setPhotoPath(request.getPhotoPath());
        if (request.getOpeningHours() != null) {
            List<OpeningHours> openingHoursList = request.getOpeningHours().stream()
                    .map(dto -> {
                        OpeningHours hours = new OpeningHours();
                        hours.setDayOfWeek(dto.getDayOfWeek());
                        hours.setOpenTime(dto.getOpenTime());
                        hours.setCloseTime(dto.getCloseTime());
                        hours.setRestaurant(restaurant);
                        return hours;
                    })
                    .toList();
            restaurant.setOpeningHours(openingHoursList);
        }
        restaurant.getOwners().add(owner);

        restaurantRepository.save(restaurant);
        return new RestaurantDetailsDTO(restaurant);
    }

    /**
     * Adds the given user as an owner of the restaurant.
     * Dev-friendly helper used to associate a logged-in Keycloak user with an existing restaurant.
     */
    public void addOwnerToRestaurant(UUID restaurantId, String userId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        restaurant.getOwners().add(owner);
        restaurantRepository.save(restaurant);
    }

    public List<String> getRestaurantOwners(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        return restaurant.getOwners().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
