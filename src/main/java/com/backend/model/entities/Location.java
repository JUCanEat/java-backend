package com.backend.model.entities;

import java.util.UUID;

import com.backend.model.valueObjects.Latitude;
import com.backend.model.valueObjects.Longitude;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Embedded
	private Latitude latitude;
	@Embedded
	private Longitude longitude;

}
