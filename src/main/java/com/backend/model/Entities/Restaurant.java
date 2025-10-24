package com.backend.model.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "restaurant")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Restaurant extends Facility {

    @ElementCollection
    private List<OpeningHours> openingHours;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Dish> dishes = new ArrayList<>();
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<DailyMenu> dailyMenus = new ArrayList<>();
    @ManyToMany(mappedBy = "favoriteRestaurants")
    private Set<User> favoritedByUsers = new HashSet<>();

}
