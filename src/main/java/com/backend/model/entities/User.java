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

    @ManyToMany
    @JoinTable(
            name = "user_favourite_facilities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> favouriteFacilities = new HashSet<>();

}
