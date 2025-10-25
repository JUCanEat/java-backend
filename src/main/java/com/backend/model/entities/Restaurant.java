package com.backend.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "restaurant")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Restaurant extends Facility {
    private String name;
    @ElementCollection
    private List<OpeningHours> openingHours;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Dish> dishes = new ArrayList<>();
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<DailyMenu> dailyMenus = new ArrayList<>();
    @ManyToMany(mappedBy = "favoriteRestaurants")
    private Set<User> favoritedByUsers = new HashSet<>();

}
