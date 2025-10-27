package com.backend.model.dtos;

import com.backend.model.entities.OpeningHours;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class OpeningHoursDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;

    public OpeningHoursDTO(OpeningHours openingHours){
        this.dayOfWeek = openingHours.getDayOfWeek();
        this.openTime = openingHours.getOpenTime();
        this.closeTime = openingHours.getCloseTime();
    }
}