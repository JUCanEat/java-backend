package com.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocalizedDishDTO {
    private DishDTO dish;
    private String displayName;
}
