package com.backend.model.dtos;

import com.backend.model.entities.Tag;
import com.backend.model.entities.UserPreference;
import jakarta.validation.constraints.NotNull;

public record PreferenceRequest(
        @NotNull Tag.TagValue tagValue,
        @NotNull UserPreference.PreferenceType preferenceType
) {}

