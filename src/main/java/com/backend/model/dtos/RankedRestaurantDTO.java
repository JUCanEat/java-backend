package com.backend.model.dtos;

import java.util.List;

public record RankedRestaurantDTO (
        RestaurantListDTO restaurant,
        double score){
}
