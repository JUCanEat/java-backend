package com.backend.model.dtos;

import com.backend.model.entities.Dish;
import com.backend.model.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIDishDTO {
    private String name;
    private String category;
    private BigDecimal price;
    private Set<String> allergens;
    private Set<String> cuisines;
    private Set<String> dietary;
}
