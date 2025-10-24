package com.backend.repositories;

import com.backend.model.entities.DailyMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, UUID> {
}
