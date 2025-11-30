package com.backend.controllers;

import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.User;
import com.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {

        UserProfileDTO profile = userService.getFullProfile(jwt);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/favourite/{facilityId}")
    public ResponseEntity<Void> addToFavourites(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID facilityId
    ) {
        userService.addToFavourites(jwt, facilityId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favourite/{facilityId}")
    public ResponseEntity<Void> removeFromFavourites(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID facilityId
    ) {
        userService.removeFromFavourites(jwt, facilityId);
        return ResponseEntity.ok().build();
    }

}
