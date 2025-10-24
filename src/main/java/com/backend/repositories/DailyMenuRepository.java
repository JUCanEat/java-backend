package com.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.entities.DailyMenu;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, UUID> {
}
