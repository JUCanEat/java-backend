package com.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.entities.Restaurant;

@Repository
public interface VendingMachineRepository extends JpaRepository<Restaurant, UUID> {
}
