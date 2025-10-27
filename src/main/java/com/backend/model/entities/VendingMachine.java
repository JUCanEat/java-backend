package com.backend.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vending_machine")
public class VendingMachine extends Facility {
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        SNACKS, COFFEE, LUNCH
    }
}
