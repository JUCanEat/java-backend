package com.backend.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@DiscriminatorValue("VENDING_MACHINE")
public class VendingMachine extends Facility {
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        SNACKS, COFFEE, LUNCH
    }
}
