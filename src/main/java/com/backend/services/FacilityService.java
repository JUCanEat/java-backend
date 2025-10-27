package com.backend.services;

import com.backend.model.dtos.FacilityLocationDTO;
import com.backend.repositories.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    public List<FacilityLocationDTO> getAllFacilities() {
        return facilityRepository.findAll().stream().map(FacilityLocationDTO::new).collect(Collectors.toList());
    }
}
