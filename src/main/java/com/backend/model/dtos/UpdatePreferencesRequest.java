package com.backend.model.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdatePreferencesRequest(
        @NotNull @Valid List<PreferenceRequest> preferences
) {}
