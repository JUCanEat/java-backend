package com.backend.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "example")
public class ExampleClass {
	@Id
	private UUID examleId;
	private String exampleField;
}
