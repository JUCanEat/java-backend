package com.backend.services;

import com.backend.model.dtos.CreateRestaurantRequest;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.dtos.UpdateRestaurantRequest;
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
    
    private static final String RESTAURANT_NOT_FOUND = "Restaurant not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_NOT_AUTHORIZED = "User not authorized to edit this restaurant";

    public List<RestaurantListDTO> getAllRestaurants(){
        return restaurantRepository.findAll().stream().map(RestaurantListDTO::new).collect(Collectors.toList());
    }

    public RestaurantDetailsDTO getRestaurantById(UUID id){
        Restaurant restaurant = findRestaurantById(id);
        return new RestaurantDetailsDTO(restaurant);
    }
    @Transactional
    public RestaurantDetailsDTO createRestaurant(CreateRestaurantRequest request, String userId) {
        User owner = findUserById(userId);

        Location location = createLocation(request.getLatitude(), request.getLongitude());

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setLocation(location);
        restaurant.setPhotoPath(request.getPhotoPath());
        if (request.getOpeningHours() != null) {
            restaurant.setOpeningHours(createOpeningHours(request.getOpeningHours(), restaurant));
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
        Restaurant restaurant = findRestaurantById(restaurantId);
        User owner = findUserById(userId);

        restaurant.getOwners().add(owner);
        restaurantRepository.save(restaurant);
    }

    public List<String> getRestaurantOwners(UUID restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        return restaurant.getOwners().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public List<RestaurantListDTO> getRestaurantsByOwner(String userId) {
        User owner = findUserById(userId);

        return owner.getOwnedRestaurants().stream()
                .map(RestaurantListDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantDetailsDTO updateRestaurant(UUID restaurantId, UpdateRestaurantRequest request, String userId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        
        validateOwnership(restaurant, userId);
        
        updateBasicFields(restaurant, request);
        updateLocationIfProvided(restaurant, request);
        updateOpeningHoursIfProvided(restaurant, request);

        restaurantRepository.save(restaurant);
        return new RestaurantDetailsDTO(restaurant);
    }
    
    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }
    
    private Restaurant findRestaurantById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, RESTAURANT_NOT_FOUND));
    }
    
    private Location createLocation(Double latitude, Double longitude) {
        Location location = new Location();
        location.setLatitude(new Latitude(latitude));
        location.setLongitude(new Longitude(longitude));
        return location;
    }
    
    private List<OpeningHours> createOpeningHours(List<com.backend.model.dtos.OpeningHoursDTO> openingHoursDTOs, Restaurant restaurant) {
        return openingHoursDTOs.stream()
                .map(dto -> {
                    OpeningHours hours = new OpeningHours();
                    hours.setDayOfWeek(dto.getDayOfWeek());
                    hours.setOpenTime(dto.getOpenTime());
                    hours.setCloseTime(dto.getCloseTime());
                    hours.setRestaurant(restaurant);
                    return hours;
                })
                .toList();
    }
    
    private void validateOwnership(Restaurant restaurant, String userId) {
        boolean isOwner = restaurant.getOwners().stream()
                .anyMatch(owner -> owner.getId().equals(userId));
        
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, USER_NOT_AUTHORIZED);
        }
    }
    
    private void updateBasicFields(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getPhotoPath() != null) {
            restaurant.setPhotoPath(request.getPhotoPath());
        }
    }
    
    private void updateLocationIfProvided(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Location location = restaurant.getLocation();
            if (location == null) {
                location = new Location();
                restaurant.setLocation(location);
            }
            location.setLatitude(new Latitude(request.getLatitude()));
            location.setLongitude(new Longitude(request.getLongitude()));
        }
    }
    
    private void updateOpeningHoursIfProvided(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.getOpeningHours() != null) {
            restaurant.getOpeningHours().clear();
            restaurant.setOpeningHours(createOpeningHours(request.getOpeningHours(), restaurant));
        }
    }
}
