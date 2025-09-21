package com.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.ExampleClass;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleClass, UUID> {
}
