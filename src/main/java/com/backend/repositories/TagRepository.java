package com.backend.repositories;

import com.backend.model.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByValue(Tag.TagValue value);
    List<Tag> findAllByValueIn(Collection<Tag.TagValue> values);

}
