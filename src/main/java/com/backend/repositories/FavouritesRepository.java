package com.backend.repositories;

import com.backend.model.Entities.Favourites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FavouritesRepository extends JpaRepository<Favourites, UUID> {
}
