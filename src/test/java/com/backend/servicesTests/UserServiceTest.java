package com.backend.servicesTests;

import com.backend.model.dtos.KeycloakUserDTO;
import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.Facility;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.model.entities.VendingMachine;
import com.backend.repositories.FacilityRepository;
import com.backend.repositories.UserRepository;
import com.backend.services.KeycloakService;
import com.backend.services.UserService;
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

    @Test
    void addToFavourites_shouldAddFacility() {
        String userId = "123";
        UUID facilityId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setFavouriteFacilities(new HashSet<>());

        Facility facility = new Restaurant();
        facility.setId(facilityId);

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        userService.addToFavourites(jwt, facilityId);

        assert(user.getFavouriteFacilities().contains(facility));
        verify(userRepository).save(user);
    }

    @Test
    void addToFavourites_shouldThrowIfFacilityNotFound() {
        String userId = "123";
        UUID facilityId = UUID.randomUUID();

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.addToFavourites(jwt, facilityId));
    }

    @Test
    void removeFromFavourites_shouldRemoveFacility() {
        String userId = "123";
        UUID facilityId = UUID.randomUUID();

        Facility facility = new Restaurant();
        facility.setId(facilityId);

        User user = new User();
        user.setId(userId);
        user.setFavouriteFacilities(new HashSet<>(Set.of(facility)));

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        userService.removeFromFavourites(jwt, facilityId);

        assertTrue(user.getFavouriteFacilities().contains(facility));
        verify(userRepository).save(user);
    }

    @Test
    void removeFromFavourites_shouldThrowIfUserNotFound() {
        UUID facilityId = UUID.randomUUID();
        Jwt jwt = Mockito.mock(Jwt.class);

        when(jwt.getSubject()).thenReturn("x");
        when(userRepository.findById("x")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.removeFromFavourites(jwt, facilityId));
    }

    @Test
    void getFullProfile_shouldReturnUserProfile() {
        String userId = "123";
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        when(jwt.getTokenValue()).thenReturn("token");

        User user = new User();
        user.setId(userId);

        Facility facility = new VendingMachine();
        facility.setId(UUID.randomUUID());
        user.setFavouriteFacilities(Set.of(facility));

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