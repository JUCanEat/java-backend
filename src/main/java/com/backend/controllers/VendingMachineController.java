package com.backend.controllers;

import com.backend.model.dtos.VendingMachineListDTO;
import com.backend.services.VendingMachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vending-machines")
@RequiredArgsConstructor
@Tag(name = "Vending Machines", description = "Endpoints for retrieving vending machines' information")
@CrossOrigin(origins = "http://localhost:3000")
public class VendingMachineController {
    private final VendingMachineService vendingMachineService;

    @Operation(
            summary = "Get all vending machines",
            description = "Retrieves a list of all available vending machines in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of vending machines")
    })
    @GetMapping
    public ResponseEntity<List<VendingMachineListDTO>> getAllVendingMachines() {
        List<VendingMachineListDTO> vendingMachines = vendingMachineService.getAllVendingMachines();
        return ResponseEntity.ok(vendingMachines);
    }
}
