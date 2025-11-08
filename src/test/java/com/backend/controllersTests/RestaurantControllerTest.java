package com.backend.controllersTests;

import com.backend.controllers.RestaurantController;
import com.backend.filters.SaveUserFilter;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.entities.Restaurant;
import com.backend.services.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers  = RestaurantController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SaveUserFilter.class),
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void shouldReturnRestaurantById() throws Exception {

        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        when(restaurantService.getRestaurantById(restaurantId)).thenReturn(new RestaurantDetailsDTO(restaurant));

        mockMvc.perform(get("/api/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    void shouldReturn404WhenRestaurantNotFound() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        when(restaurantService.getRestaurantById(restaurantId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        mockMvc.perform(get("/api/restaurants/{id}", restaurantId))
                .andExpect(status().isNotFound());
    }
}