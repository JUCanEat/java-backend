package com.backend.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vending_machine")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class VendingMachine extends Facility {
    @Enumerated(EnumType.STRING)
    private Type type;

    private enum Type {
        SNACKS,
        COFFEE,
        LUNCH
    }
}
