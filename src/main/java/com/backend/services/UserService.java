package com.backend.services;

import com.backend.model.dtos.FacilityDTO;
import com.backend.model.dtos.KeycloakUserDTO;
import com.backend.model.dtos.PreferenceRequest;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.dtos.UpdatePreferencesRequest;
import com.backend.model.dtos.UserPreferenceDTO;
import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.Facility;
import com.backend.model.entities.Tag;
import com.backend.model.entities.User;
import com.backend.model.entities.UserPreference;
import com.backend.repositories.FacilityRepository;
import com.backend.repositories.TagRepository;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final TagRepository tagRepository;
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

        String email = token.getClaimAsString("email");
        String firstName = token.getClaimAsString("given_name");
        String lastName = token.getClaimAsString("family_name");
        String username = token.getClaimAsString("preferred_username");

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
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
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

    @Transactional
    public void updatePreferences(Jwt jwt, UpdatePreferencesRequest request) {
        String userId = jwt.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Set<Tag.TagValue> requestedValues = request.preferences().stream()
                .map(PreferenceRequest::tagValue)
                .collect(Collectors.toSet());

        Map<Tag.TagValue, Tag> tagsByValue = tagRepository.findAllByValueIn(requestedValues).stream()
                .collect(Collectors.toMap(Tag::getValue, t -> t));

        Map<Tag.TagValue, UserPreference.PreferenceType> requested = request.preferences().stream()
                .collect(Collectors.toMap(PreferenceRequest::tagValue, PreferenceRequest::preferenceType));


        Map<Tag.TagValue, UserPreference> existing = user.getPreferences().stream()
                .collect(Collectors.toMap(p -> p.getTag().getValue(), p -> p));

        // delete preferences not included in request
        existing.entrySet().stream()
                .filter(e -> !requested.containsKey(e.getKey()))
                .forEach(e -> user.removePreference(e.getValue()));

        // add new or update
        requested.forEach((tagValue, preferenceType) -> {
            if (existing.containsKey(tagValue)) {
                // type changed (INCLUDE -> EXCLUDE)
                existing.get(tagValue).setPreferenceType(preferenceType);
            } else {
                Tag tag = tagsByValue.get(tagValue);
                if (tag == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown tag: " + tagValue);
                }
                user.addPreference(UserPreference.builder()
                        .user(user)
                        .tag(tag)
                        .preferenceType(preferenceType)
                        .build());
            }
        });
    }


    public List<UserPreferenceDTO> getPreferences(Jwt jwt) {
        String userId = jwt.getSubject();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getPreferences().stream()
                .map(UserPreferenceDTO::from)
                .toList();
    }

}