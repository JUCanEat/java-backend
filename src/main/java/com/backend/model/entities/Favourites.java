package com.backend.model.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "favourites")
public class Favourites {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@ElementCollection
	private List<Restaurant> restaurants;
	@ElementCollection
	private List<Dish> dishes;

}
