package com.backend.model.dtos;

import com.backend.model.entities.DishTranslation;
import com.backend.model.entities.Tag;
import com.backend.model.entities.Tag.TagType;
import com.backend.model.entities.Tag.TagValue;

public record TagDTO(
        TagValue value,
        TagType tagType,
        String label
) {
    public static TagDTO from(Tag tag, DishTranslation.Language language) {
        return new TagDTO(
                tag.getValue(),
                tag.getTagType(),
                tag.getValue().getLabel(language)
        );
    }
}

