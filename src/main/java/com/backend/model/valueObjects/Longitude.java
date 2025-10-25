package com.backend.model.valueObjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Longitude {
	@Column(name = "longitude")
	private Double value;

	public Longitude(Double value) {
		if (value == null || value < -180 || value > 180) {
			throw new IllegalArgumentException("Longitude must be between -180 and 180");
		}
		this.value = value;
	}
}
