package com.backend.repositories;

import com.backend.model.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    List<Restaurant> findAllByOwners_Id(String ownerId);

    @Query("""
    SELECT DISTINCT r FROM Restaurant r
    LEFT JOIN FETCH r.dailyMenus dm
    LEFT JOIN FETCH dm.dishes d
    LEFT JOIN FETCH d.tags
    WHERE dm.status = 'ACTIVE'
    """)
    List<Restaurant> findAllRestaurantsWithTodayMenu();
}
