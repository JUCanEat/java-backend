package com.backend.services;

import com.backend.model.dtos.KeycloakUserDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.repositories.RestaurantRepository;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final RestaurantRepository restaurantRepository;
    @Transactional
    public User getOrCreateUserFromJwt(Jwt jwt) {
        log.info("In filter");
        String userId = jwt.getSubject();

        return userRepository.findById(userId)
                .orElseGet(() -> createUserFromJwt(jwt));
    }

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

        List<Restaurant> ownedRestaurants = restaurantRepository.findAllByOwners_Id(userId);

        return UserProfileDTO.builder()
                .id(userId)
                .email(keycloakUser.getEmail())
                .firstName(keycloakUser.getGivenName())
                .lastName(keycloakUser.getFamilyName())
                .username(keycloakUser.getPreferredUsername())
               // .favouriteRestaurants(favouriteRestaurants)
                .ownedRestaurants(ownedRestaurants.stream().map(RestaurantListDTO::new).toList())
                .build();
    }

}