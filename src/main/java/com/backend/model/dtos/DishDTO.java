package com.backend.model.dtos;

import com.backend.model.entities.Dish;
import com.backend.model.valueObjects.Price;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class DishDTO {
    private UUID id;
    private String name;
    private String category;
    private Price price;
    private Set<String> allergens;

    public DishDTO(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.category = dish.getCategory().name();
        this.price = dish.getPrice();
        this.allergens = dish.getAllergens() != null
                ? dish.getAllergens().stream()
                .map(Enum::name)
                .collect(Collectors.toSet())
                : Set.of();
    }
}
