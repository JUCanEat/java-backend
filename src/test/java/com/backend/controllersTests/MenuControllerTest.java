package com.backend.controllersTests;

import com.backend.controllers.MenuController;
import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuService menuService;

    @Test
    void shouldReturnDailyMenuWhenExists() throws Exception {
        UUID restaurantId = UUID.fromString("b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1");
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        DailyMenu menu= new DailyMenu();
        menu.setId(UUID.randomUUID());
        menu.setRestaurant(restaurant);
        menu.setDate(LocalDate.now());
        menu.setStatus(DailyMenu.Status.ACTIVE);

        when(menuService.getDailyMenuByRestaurantId(restaurantId)).thenReturn(new DailyMenuDTO(menu));

        mockMvc.perform(get("/api/menus/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(menu.getId().toString()));

        verify(menuService).getDailyMenuByRestaurantId(restaurantId);
    }

    @Test
    void shouldReturn404WhenMenuNotFound() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(menuService.getDailyMenuByRestaurantId(restaurantId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Daily menu not found"));

        mockMvc.perform(get("/api/menus/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(menuService).getDailyMenuByRestaurantId(restaurantId);
    }

    @Test
    void shouldReturn500WhenMultipleActiveMenusFound() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(menuService.getDailyMenuByRestaurantId(restaurantId))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "More than one daily menu found"
                ));

        mockMvc.perform(get("/api/menus/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(menuService).getDailyMenuByRestaurantId(restaurantId);
    }
}