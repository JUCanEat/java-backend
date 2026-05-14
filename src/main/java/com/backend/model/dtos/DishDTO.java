package com.backend.model.dtos;

import com.backend.model.entities.Dish;
import com.backend.model.entities.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DishDTO {
    private UUID id;
    private String name;
    private String category;
    private BigDecimal price;
    private Set<Tag> tags;

    public DishDTO(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.category = dish.getCategory().name();
        this.price = dish.getPrice().getAmount();
        this.tags = dish.getTags();
    }
}
