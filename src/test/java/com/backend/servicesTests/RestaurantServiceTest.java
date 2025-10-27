package com.backend.servicesTests;

import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.RestaurantRepository;
import com.backend.services.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private DailyMenu dailyMenu;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());
        restaurant.setName("Testowa Restauracja");
        restaurant.setDescription("Pyszne jedzenie");

        dailyMenu = new DailyMenu();
        dailyMenu.setId(UUID.randomUUID());
        dailyMenu.setStatus(DailyMenu.Status.ACTIVE);

        List<DailyMenu> dailyMenus = new ArrayList<>();
        dailyMenus.add(dailyMenu);
        restaurant.setDailyMenus(dailyMenus);

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
}