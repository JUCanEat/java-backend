package com.backend.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "restaurant")
public class Restaurant extends Facility {
    private String name;
    @OneToMany(mappedBy = "restaurant")
    private List<OpeningHours> openingHours;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Dish> dishes = new ArrayList<>();
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<DailyMenu> dailyMenus = new ArrayList<>();
    @ManyToMany(mappedBy = "favoriteRestaurants")
    private Set<User> favoritedByUsers = new HashSet<>();
    @OneToMany
    private Set<User> owners;

    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        if (openingHours == null || openingHours.isEmpty()) {
            return false;
        }

        return openingHours.stream()
                .filter(oh -> oh.getDayOfWeek() == today)
                .anyMatch(oh -> isWithinOpeningHours(currentTime, oh));
    }

    private boolean isWithinOpeningHours(LocalTime currentTime, OpeningHours hours) {
        return !currentTime.isBefore(hours.getOpenTime())
                && !currentTime.isAfter(hours.getCloseTime());
    }

    public DailyMenu getTodayMenu(){
        return dailyMenus.stream()
                    .filter(DailyMenu::isActive)
                    .findFirst()
                    .orElse(null);
    }

}
