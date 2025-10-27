package com.backend.servicesTests;

import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.DailyMenuRepository;
import com.backend.services.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuServiceTest {

    @Mock
    private DailyMenuRepository dailyMenuRepository;

    @InjectMocks
    private MenuService menuService;
    private DailyMenu dailyMenu;
    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantId = UUID.randomUUID();
        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setId(UUID.randomUUID());
        activeMenu.setRestaurant(restaurant);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);
        activeMenu.setDate(LocalDate.now());

        dailyMenu = activeMenu;
    }

    @Test
    void shouldReturnDailyMenu() {

        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE))
                .thenReturn(Optional.of(dailyMenu));

        DailyMenuDTO result = menuService.getDailyMenuByRestaurantId(restaurantId);

        assertThat(result).isNotNull();
        verify(dailyMenuRepository).findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE);
    }

    @Test
    void shouldThrowExceptionWhenMenuNotFound() {
        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getDailyMenuByRestaurantId(restaurantId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }
}