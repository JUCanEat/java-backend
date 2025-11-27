package com.backend.model.dtos;

import com.backend.model.entities.Location;
import com.backend.model.entities.VendingMachine;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class VendingMachineListDTO {
    private UUID id;
    private String description;
    private String photoPath;
    private Location location;

    public VendingMachineListDTO(VendingMachine vendingMachine) {
        this.id = vendingMachine.getId();
        this.description = vendingMachine.getDescription();
        this.photoPath = vendingMachine.getPhotoPath();
        this.location = vendingMachine.getLocation();
    }
}