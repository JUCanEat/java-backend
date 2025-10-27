package com.backend.model.dtos;

import com.backend.model.entities.Facility;
import com.backend.model.entities.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class FacilityLocationDTO {
    private UUID id;
    private FacilityType type;
    private String name;
    private Double latitude;
    private Double longitude;

    public FacilityLocationDTO(Facility facility){
        this.id = facility.getId();
        if (facility instanceof Restaurant){
            this.type = FacilityType.RESTAURANT;
            this.name = ((Restaurant) facility).getName();
        } else {
            this.type = FacilityType.VENDING_MACHINE;
            this.name = null;
        }
        this.longitude = facility.getLocation().getLongitude().getValue();
        this.latitude = facility.getLocation().getLatitude().getValue();
    }

    public enum FacilityType {
        RESTAURANT,
        VENDING_MACHINE
    }
}