package com.backend.repositories;

import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {
    Optional<List<Dish>> findByRestaurantId(UUID id);
}
