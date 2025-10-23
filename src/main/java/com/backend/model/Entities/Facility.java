package com.backend.model.Entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.Embedded;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class Facility {

    @Id
    @GeneratedValue
    private UUID id;
    private String description;
    private String photoPath;
    @OneToOne
    private Location location;
}
