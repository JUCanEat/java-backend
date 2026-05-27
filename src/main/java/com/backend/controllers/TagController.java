package com.backend.controllers;

import com.backend.model.dtos.TagDTO;
import com.backend.model.entities.DishTranslation;
import com.backend.model.entities.Tag;
import com.backend.services.TagService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Endpoints for retrieving tags")
@CrossOrigin(origins = "http://localhost:3000")
public class TagController {
    private final TagService tagService;
    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(
            @Parameter(description = "Language code (PL or EN)", required = false)
            @RequestParam(value = "language", defaultValue = "EN") DishTranslation.Language language) {
        return ResponseEntity.ok(tagService.getAllTags(language));
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<Tag.TagType, List<TagDTO>>> getAllTagsGrouped(
            @Parameter(description = "Language code (PL or EN)", required = false)
            @RequestParam(value = "language", defaultValue = "EN") DishTranslation.Language language) {
        return ResponseEntity.ok(tagService.getAllTagsGrouped(language));
    }

}
