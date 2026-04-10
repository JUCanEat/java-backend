package com.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LocalizedDailyMenuDTO {
    private UUID id;
    private LocalDate date;
    private String language;
    private List<LocalizedDishDTO> dishes;
}
