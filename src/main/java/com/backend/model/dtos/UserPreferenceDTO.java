package com.backend.model.dtos;

import com.backend.model.entities.Tag;
import com.backend.model.entities.UserPreference;

public record UserPreferenceDTO(
        Tag.TagValue tagValue,
        Tag.TagType tagType,
        UserPreference.PreferenceType preferenceType
) {
    public static UserPreferenceDTO from(UserPreference preference) {
        return new UserPreferenceDTO(
                preference.getTag().getValue(),
                preference.getTag().getTagType(),
                preference.getPreferenceType()
        );
    }
}
