package com.backend.model.entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Facility {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private String description;
	private String photoPath;
	@OneToOne
	private Location location;

}
