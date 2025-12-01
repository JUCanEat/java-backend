package com.backend.servicesTests;

import com.backend.model.dtos.KeycloakUserDTO;
import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.Facility;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.repositories.FacilityRepository;
import com.backend.repositories.UserRepository;
import com.backend.services.KeycloakService;
import com.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    FacilityRepository facilityRepository;

    @Mock
    KeycloakService keycloakService;

    @InjectMocks
    UserService userService;

    private String userId;
    private UUID facilityId;
    private User user;
    private Facility facility;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        userId = "123";
        facilityId = UUID.randomUUID();

        jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);

        user = new User();
        user.setId(userId);
        user.setFavouriteFacilities(new HashSet<>());

        facility = new Restaurant();
        facility.setId(facilityId);
    }

    @Test
    void addToFavourites_shouldAddFacility() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        userService.addToFavourites(jwt, facilityId);

        assertTrue(user.getFavouriteFacilities().contains(facility));
        verify(userRepository).save(user);
    }

    @Test
    void addToFavourites_shouldThrowIfFacilityNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.addToFavourites(jwt, facilityId));
    }

    @Test
    void removeFromFavourites_shouldRemoveFacility() {
        user.getFavouriteFacilities().add(facility);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        userService.removeFromFavourites(jwt, facilityId);

        assertFalse(user.getFavouriteFacilities().contains(facility));
        verify(userRepository).save(user);
    }

    @Test
    void removeFromFavourites_shouldThrowIfUserNotFound() {
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.removeFromFavourites(jwt, facilityId));
    }

    @Test
    void getFullProfile_shouldReturnUserProfile() {
        when(jwt.getTokenValue()).thenReturn("token");

        User user = new User();
        user.setId(userId);

        user.getFavouriteFacilities().add(facility);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());
        user.setOwnedRestaurants(Set.of(restaurant));

        KeycloakUserDTO kcDto = new KeycloakUserDTO();
        kcDto.setEmail("test@mail.com");
        kcDto.setGivenName("John");
        kcDto.setFamilyName("Doe");
        kcDto.setPreferredUsername("johndoe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(keycloakService.getUserInfo("token")).thenReturn(kcDto);

        UserProfileDTO dto = userService.getFullProfile(jwt);

        assertEquals("123", dto.getId());
        assertEquals("test@mail.com", dto.getEmail());
        assertEquals(1, dto.getFavourites().size());
        assertEquals(1, dto.getOwnedRestaurants().size());
    }
}