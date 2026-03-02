package com.backend.servicesTests;

import com.backend.model.dtos.CreateRestaurantRequest;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.dtos.UpdateRestaurantRequest;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Location;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.model.valueObjects.Latitude;
import com.backend.model.valueObjects.Longitude;
import com.backend.repositories.RestaurantRepository;
import com.backend.repositories.UserRepository;
import com.backend.services.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private DailyMenu dailyMenu;
    private User user;
    private String userId;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        userId = "test-user-123";
        
        // Setup user
        user = new User();
        user.setId(userId);
        user.setOwnedRestaurants(new HashSet<>());

        // Setup restaurant
        restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());
        restaurant.setName("Testowa Restauracja");
        restaurant.setDescription("Pyszne jedzenie");
        
        Location location = new Location();
        location.setLatitude(new Latitude(50.0614));
        location.setLongitude(new Longitude(19.9366));
        restaurant.setLocation(location);
        
        Set<User> owners = new HashSet<>();
        owners.add(user);
        restaurant.setOwners(owners);
        user.getOwnedRestaurants().add(restaurant);

        dailyMenu = new DailyMenu();
        dailyMenu.setId(UUID.randomUUID());
        dailyMenu.setStatus(DailyMenu.Status.ACTIVE);

        List<DailyMenu> dailyMenus = new ArrayList<>();
        dailyMenus.add(dailyMenu);
        restaurant.setDailyMenus(dailyMenus);

    }

    private Jwt createMockJwt(String subject) {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(subject);
        return jwt;
    }

    @Test
    void shouldReturnAllRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<RestaurantListDTO> result = restaurantService.getAllRestaurants();

        assertEquals(1, result.size());
        assertEquals("Testowa Restauracja", result.get(0).getName());
        verify(restaurantRepository).findAll();
    }

    @Test
    void shouldReturnRestaurantDetailsById() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        RestaurantDetailsDTO dto = restaurantService.getRestaurantById(restaurant.getId());

        assertEquals(restaurant.getId(), dto.getId());
        assertEquals("Testowa Restauracja", dto.getName());
        verify(restaurantRepository).findById(restaurant.getId());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.getRestaurantById(id)
        );

        assertEquals("404 NOT_FOUND \"Restaurant not found\"", ex.getMessage());
        verify(restaurantRepository).findById(id);
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        request.setName("New Restaurant");
        request.setDescription("Great food");
        request.setLatitude(50.0614);
        request.setLongitude(19.9366);
        request.setPhotoPath("/path/to/photo.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantDetailsDTO result = restaurantService.createRestaurant(request, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingRestaurantWithInvalidUser() {
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        request.setName("New Restaurant");
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.createRestaurant(request, userId)
        );

        assertEquals("404 NOT_FOUND \"User not found\"", ex.getMessage());
    }

    @Test
    void shouldGetRestaurantsByOwner() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<RestaurantListDTO> result = restaurantService.getRestaurantsByOwner(userId);

        assertEquals(1, result.size());
        assertEquals("Testowa Restauracja", result.get(0).getName());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenGettingRestaurantsByInvalidOwner() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.getRestaurantsByOwner(userId)
        );

        assertEquals("404 NOT_FOUND \"User not found\"", ex.getMessage());
    }

    @Test
    void shouldUpdateRestaurantSuccessfully() {
        UpdateRestaurantRequest request = new UpdateRestaurantRequest();
        request.setName("Updated Restaurant");
        request.setDescription("Updated description");
        request.setPhotoPath("/new/path/photo.jpg");
        request.setLatitude(51.0);
        request.setLongitude(20.0);

        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantDetailsDTO result = restaurantService.updateRestaurant(restaurant.getId(), request, userId);

        assertNotNull(result);
        verify(restaurantRepository).findById(restaurant.getId());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentRestaurant() {
        UUID nonExistentId = UUID.randomUUID();
        UpdateRestaurantRequest request = new UpdateRestaurantRequest();
        request.setName("Updated Restaurant");

        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.updateRestaurant(nonExistentId, request, userId)
        );

        assertEquals("404 NOT_FOUND \"Restaurant not found\"", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRestaurantByUnauthorizedUser() {
        String unauthorizedUserId = "unauthorized-user";
        UpdateRestaurantRequest request = new UpdateRestaurantRequest();
        request.setName("Updated Restaurant");

        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.updateRestaurant(restaurant.getId(), request, unauthorizedUserId)
        );

        assertEquals("403 FORBIDDEN \"User not authorized to edit this restaurant\"", ex.getMessage());
    }

    @Test
    void shouldAddOwnerToRestaurantSuccessfully() {
        User newOwner = new User();
        newOwner.setId("new-owner-123");
        newOwner.setOwnedRestaurants(new HashSet<>());

        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(userRepository.findById("new-owner-123")).thenReturn(Optional.of(newOwner));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        restaurantService.addOwnerToRestaurant(restaurant.getId(), "new-owner-123");

        assertTrue(restaurant.getOwners().contains(newOwner));
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenAddingOwnerToNonExistentRestaurant() {
        UUID nonExistentId = UUID.randomUUID();
        
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.addOwnerToRestaurant(nonExistentId, "some-user")
        );

        assertEquals("404 NOT_FOUND \"Restaurant not found\"", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddingNonExistentUserAsOwner() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(userRepository.findById("non-existent-user")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.addOwnerToRestaurant(restaurant.getId(), "non-existent-user")
        );

        assertEquals("404 NOT_FOUND \"User not found\"", ex.getMessage());
    }

    @Test
    void shouldGetRestaurantOwnersSuccessfully() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        List<String> owners = restaurantService.getRestaurantOwners(restaurant.getId());

        assertEquals(1, owners.size());
        assertTrue(owners.contains(userId));
        verify(restaurantRepository).findById(restaurant.getId());
    }

    @Test
    void shouldThrowExceptionWhenGettingOwnersOfNonExistentRestaurant() {
        UUID nonExistentId = UUID.randomUUID();
        
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                restaurantService.getRestaurantOwners(nonExistentId)
        );

        assertEquals("404 NOT_FOUND \"Restaurant not found\"", ex.getMessage());
    }
}