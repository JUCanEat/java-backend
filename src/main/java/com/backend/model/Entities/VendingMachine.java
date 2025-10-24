package com.backend.model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vending_machine")
public class VendingMachine extends Facility {
    @Enumerated(EnumType.STRING)
    private Type type;

    private enum Type {
        SNACKS,
        COFFEE,
        LUNCH
    }
}
