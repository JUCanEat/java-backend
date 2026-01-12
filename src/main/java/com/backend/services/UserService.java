package com.backend.services;

import com.backend.model.dtos.FacilityDTO;
import com.backend.model.dtos.KeycloakUserDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.Facility;
import com.backend.model.entities.User;
import com.backend.repositories.FacilityRepository;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final FacilityRepository facilityRepository;
    @Transactional
    public User getOrCreateUserFromJwt(Jwt jwt) {
        log.info("In filter");
        String userId = jwt.getSubject();

        return userRepository.findById(userId)
                .orElseGet(() -> createUserFromJwt(jwt));
    }
    @Transactional
    private User createUserFromJwt(Jwt jwt) {
        User user = new User();
        user.setId(jwt.getSubject());
        log.info("User {} created." , user.getId());
        return userRepository.save(user);
    }

    @Transactional
    public UserProfileDTO getFullProfile(Jwt token) {
        String userId = token.getSubject();
        String accessToken = token.getTokenValue();

        KeycloakUserDTO keycloakUser = keycloakService.getUserInfo(accessToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<FacilityDTO> favourites = user.getFavouriteFacilities().stream()
                .map(FacilityDTO::new)
                .toList();

        List<RestaurantListDTO> ownedRestaurants = user.getOwnedRestaurants().stream()
                .map(RestaurantListDTO::new)
                .toList();

        return UserProfileDTO.builder()
                .id(userId)
                .email(keycloakUser.getEmail())
                .firstName(keycloakUser.getGivenName())
                .lastName(keycloakUser.getFamilyName())
                .username(keycloakUser.getPreferredUsername())
                .favourites(favourites)
                .ownedRestaurants(ownedRestaurants)
                .build();
    }

    @Transactional
    public UUID getFirstOwnedRestaurant(Jwt jwt) {
        String userId = jwt.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getOwnedRestaurants().stream()
                .findFirst()
                .map(restaurant -> restaurant.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has no owned restaurants"));
    }

    @Transactional
    public void addToFavourites(Jwt jwt, UUID facilityId) {
        String userId = jwt.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found"));

        user.getFavouriteFacilities().add(facility);
        userRepository.save(user);
    }
    @Transactional
    public void removeFromFavourites(Jwt jwt, UUID facilityId) {
        String userId = jwt.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found"));

        user.getFavouriteFacilities().remove(facility);
        userRepository.save(user);
    }
}