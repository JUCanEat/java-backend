package com.backend.model.entities;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "keycloak_user")
public class User {
    @Id
    private UUID id;

    private String email;
    private String username;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_restaurants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    private Set<Restaurant> favoriteRestaurants = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
