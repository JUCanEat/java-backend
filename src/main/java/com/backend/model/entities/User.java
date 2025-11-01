package com.backend.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "keycloak_user")
public class User {
    @Id
    private String id;

    @ManyToMany(mappedBy = "owners")
    private Set<Restaurant> ownedRestaurants = new HashSet<>();

    //private String email; jeszcze nie wiem czy potrzebujemy to trzymać
    //private String username;
    //private LocalDateTime createdAt;
    //private LocalDateTime lastLoginAt;

    @ManyToMany
    @JoinTable(name = "user_favorite_restaurants", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
    private Set<Restaurant> favoriteRestaurants = new HashSet<>();


}
