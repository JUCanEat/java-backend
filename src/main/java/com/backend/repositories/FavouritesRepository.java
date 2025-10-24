package com.backend.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.entities.Favourites;

@Repository
public interface FavouritesRepository extends JpaRepository<Favourites, UUID> {
}
