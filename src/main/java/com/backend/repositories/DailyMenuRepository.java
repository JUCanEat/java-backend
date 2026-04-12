package com.backend.repositories;

import com.backend.model.entities.DailyMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, UUID> {

    List<DailyMenu> findAllByStatusAndDate(DailyMenu.Status status, LocalDate date);
    List<DailyMenu> findByRestaurantIdAndStatusInOrderByDateAsc(UUID restaurantId, List<DailyMenu.Status> statuses);
    Optional<DailyMenu> findByRestaurantIdAndStatus(UUID id, DailyMenu.Status status);
    Optional<DailyMenu> findByRestaurantIdAndStatusAndDate(UUID id, DailyMenu.Status status, LocalDate date);
    Optional<DailyMenu> findByIdAndRestaurantId(UUID menuId, UUID restaurantId);
    void deleteByRestaurantIdAndStatus(UUID id, DailyMenu.Status status);
    void deleteByRestaurantIdAndStatusAndDate(UUID id, DailyMenu.Status status, LocalDate date);
}
