package com.backend.controllers;

import com.backend.model.dtos.UserProfileDTO;
import com.backend.model.entities.User;
import com.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentProfile(
            @AuthenticationPrincipal Jwt jwt) {

        UserProfileDTO profile = userService.getFullProfile(jwt);
        return ResponseEntity.ok(profile);
    }


}
