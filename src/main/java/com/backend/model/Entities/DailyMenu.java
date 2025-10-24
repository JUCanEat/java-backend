package com.backend.model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "daily_menu")
public class DailyMenu {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToMany
    List<Dish> menuItems;
}
