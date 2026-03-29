package com.backend.repositories;

import com.backend.model.entities.DishTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DishTranslationRepository extends JpaRepository<DishTranslation, UUID> {
    Optional<DishTranslation> findByDishIdAndLanguage(UUID dishId, DishTranslation.Language language);
    List<DishTranslation> findByDishIdInAndLanguage(List<UUID> dishIds, DishTranslation.Language language);
}
