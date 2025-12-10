package com.backend.servicesTests;

import com.backend.model.dtos.VendingMachineListDTO;
import com.backend.model.entities.VendingMachine;
import com.backend.repositories.VendingMachineRepository;
import com.backend.services.VendingMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VendingMachineServiceTest {

    @Mock
    private VendingMachineRepository vendingMachineRepository;

    @InjectMocks
    private VendingMachineService vendingMachineService;

    private VendingMachine vendingMachine;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vendingMachine = new VendingMachine();
        vendingMachine.setId(UUID.randomUUID());
        vendingMachine.setDescription("Sweet and savoury treats between classes");
    }

    @Test
    void shouldReturnAllVendingMachines() {
        when(vendingMachineRepository.findAll()).thenReturn(List.of(vendingMachine));

        List<VendingMachineListDTO> result = vendingMachineService.getAllVendingMachines();

        assertEquals(1, result.size());
        assertEquals("Sweet and savoury treats between classes", result.get(0).getDescription());
        verify(vendingMachineRepository).findAll();
    }
}