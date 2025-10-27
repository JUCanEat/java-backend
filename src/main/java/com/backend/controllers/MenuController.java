package com.backend.controllers;

import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.RestaurantDetailsDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.services.MenuService;
import com.backend.services.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "Endpoints for retrieving restaurants daily menu")
public class MenuController {
    private final MenuService menuService;
    @Operation(
            summary = "Get restaurant daily menu by restaurant ID",
            description = "Retrieves daily menu for given restaurant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved restaurant details"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DailyMenuDTO> getDailyMenuByRestaurantI(
            @Parameter(description = "Id of the restaurant", required = true)
            @PathVariable UUID id) {

        DailyMenuDTO dailyMenu = menuService.getDailyMenuByRestaurantId(id);
        return ResponseEntity.ok(dailyMenu);
    }
}
