package com.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class LocalizedDishDTO {
    private DishDTO dish;
    private String displayName;
    private BigDecimal price;
}
