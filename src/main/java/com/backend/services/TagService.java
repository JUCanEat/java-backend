package com.backend.services;

import com.backend.model.dtos.TagDTO;
import com.backend.model.entities.DishTranslation;
import com.backend.model.entities.Tag.TagType;
import com.backend.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagDTO> getAllTags(DishTranslation.Language language) {
        return tagRepository.findAll().stream()
                .map(tag -> TagDTO.from(tag, language))
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<TagType, List<TagDTO>> getAllTagsGrouped(DishTranslation.Language language) {
        return tagRepository.findAll().stream()
                .map(tag -> TagDTO.from(tag, language))
                .collect(Collectors.groupingBy(TagDTO::tagType));
    }
}
