package com.backend.controllers;


import com.backend.model.dtos.FacilityLocationDTO;
import com.backend.services.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
@Tag(name = "Facilities", description = "Endpoints for retrieving facilities")
public class FacilityController {
    private final FacilityService facilityService;
    @Operation(
            summary = "Get all facilities",
            description = "Retrieves a list of all facilities with location the purpose of the map."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of facilities")
    })
    @GetMapping
    public ResponseEntity<List<FacilityLocationDTO>> getAllFacilities() {
        List<FacilityLocationDTO> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }
}

