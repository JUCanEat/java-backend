package com.backend.model.Entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "restaurant")
public class Restaurant extends Facility {
    private String name;
    @ElementCollection
    private List<OpeningHours> openingHours;
}
