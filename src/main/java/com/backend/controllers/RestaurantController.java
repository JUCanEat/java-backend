package com.backend.controllers;

import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.services.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Endpoints for retrieving restaurant information")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
            summary = "Get all restaurants",
            description = "Retrieves a list of all available restaurants in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of restaurants")
    })
    @GetMapping
    public ResponseEntity<List<RestaurantListDTO>> getAllRestaurants() {
        List<RestaurantListDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(
            summary = "Get restaurant details by ID",
            description = "Retrieves detailed information about a specific restaurant, including location, opening hours, and today's menu."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved restaurant details"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetailsDTO> getRestaurantById(
            @Parameter(description = "Id of the restaurant", required = true)
            @PathVariable UUID id) {

        RestaurantDetailsDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @Operation(
            summary = "Add daily menu for restaurant by ID",
            description = "Ads new daily menu for the restaurant with given id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved restaurant details"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "403", description = "User is not the owner of the restaurant"),
    })

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('restaurant_owner')")
    public ResponseEntity<RestaurantDetailsDTO> AddDailyMenu(
            @Parameter(description = "Id of the restaurant", required = true)
            @PathVariable UUID id) {

        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}