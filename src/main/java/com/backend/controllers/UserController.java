package com.backend.controllers;

import com.backend.model.dtos.UserProfileDTO;
import com.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for user specific actions")
public class UserController {
    private final UserService userService;
    @Operation(
            summary = "Get user profile",
            description = "Retrieves user info from keycloak, includes likes and owned restaurants."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user profile")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {

        UserProfileDTO profile = userService.getFullProfile(jwt);
        return ResponseEntity.ok(profile);
    }
    @Operation(
            summary = "Add facility to favourites",
            description = "Adds a facility to the list of favourite facilities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added facility to favourites")
    })
    @PostMapping("/favourites/{facilityId}")
    public ResponseEntity<Void> addToFavourites(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID facilityId
    ) {
        userService.addToFavourites(jwt, facilityId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Remove facility from favourites",
            description = "Removes a facility from the list of favourite facilities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed facility from favourites")
    })
    @DeleteMapping("/favourites/{facilityId}")
    public ResponseEntity<Void> removeFromFavourites(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID facilityId
    ) {
        userService.removeFromFavourites(jwt, facilityId);
        return ResponseEntity.ok().build();
    }

}
