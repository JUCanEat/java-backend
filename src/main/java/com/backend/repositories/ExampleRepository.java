package com.backend.repositories;

import com.backend.model.ExampleClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleClass, UUID> {

}
