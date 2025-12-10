package com.backend.model.dtos;

import com.backend.model.entities.Facility;
import com.backend.model.entities.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class FacilityDTO {
    private UUID id;
    private String description;
    private String photoPath;
    private FacilityType type;
    private String name;

    public FacilityDTO(Facility facility){
        this.id = facility.getId();
        if (facility instanceof Restaurant){
            this.type = FacilityType.RESTAURANT;
            this.name = ((Restaurant) facility).getName();
        } else {
            this.type = FacilityType.VENDING_MACHINE;
            this.name = null;
        }
        this.photoPath = facility.getPhotoPath();
        this.description = facility.getDescription();
    }

    public enum FacilityType {
        RESTAURANT,
        VENDING_MACHINE
    }
}
