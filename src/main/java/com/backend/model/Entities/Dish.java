package com.backend.model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Category category;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Set<Allergens> allergens;

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

    @PreUpdate
    private void validatePrice () {
        if (price != null && price.signum() <= 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
