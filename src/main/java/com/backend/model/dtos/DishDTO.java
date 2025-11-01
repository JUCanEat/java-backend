package com.backend.model.dtos;

import com.backend.model.entities.Dish;
import com.backend.model.valueObjects.Price;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class DishDTO {
    private UUID id;
    private String name;
    private String category;
    private BigDecimal price;
    private Set<Dish.Allergens> allergens;

    public DishDTO(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.category = dish.getCategory().name();
        this.price = dish.getPrice().getAmount();
        this.allergens = dish.getAllergens();
    }
}
