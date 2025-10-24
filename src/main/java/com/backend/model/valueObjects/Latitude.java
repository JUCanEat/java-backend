package com.backend.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Latitude {

	private Double value;

	public Latitude(Double value) {
		if (value == null || value < -90 || value > 90) {
			throw new IllegalArgumentException("Latitude must be between -90 and 90");
		}
		this.value = value;
	}
}
