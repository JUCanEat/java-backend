package com.backend.services;

import com.backend.model.dtos.VendingMachineListDTO;
import com.backend.repositories.VendingMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendingMachineService {
    private final VendingMachineRepository vendingMachineRepository;

    public List<VendingMachineListDTO> getAllVendingMachines(){
        return vendingMachineRepository.findAll().stream().map(VendingMachineListDTO::new).collect(Collectors.toList());
    }
}
