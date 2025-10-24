package com.backend.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
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
@Table(name = "daily_menu")
public class DailyMenu {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private LocalDate date;
	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToMany
	@JoinTable(name = "daily_menu_dishes", joinColumns = @JoinColumn(name = "daily_menu_id"), inverseJoinColumns = @JoinColumn(name = "dish_id"))
	private List<Dish> dishes = new ArrayList<>();
	@ManyToOne
	private Restaurant restaurant;

	private enum Status {
		ACTIVE, INACTIVE
	}
}
