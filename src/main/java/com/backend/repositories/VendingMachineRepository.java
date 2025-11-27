package com.backend.repositories;

import com.backend.model.entities.Restaurant;
import com.backend.model.entities.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendingMachineRepository extends JpaRepository<VendingMachine, UUID> {
}
