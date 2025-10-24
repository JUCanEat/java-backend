package com.backend.model.entities;

import com.backend.model.valueObjects.Price;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Embedded
    private Price price;


    @ElementCollection(targetClass = Allergens.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "dish_allergens")
    @Column(name = "allergen")
    private Set<Allergens> allergens;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "dishes")
    private List<DailyMenu> dailyMenus = new ArrayList<>();

    private enum Category {
        SOUP,
        MAIN_COURSE
    }

    private enum Allergens {
        NUTS,
        GLUTEN,
        MEAT,
        LACTOSE
    }
}
