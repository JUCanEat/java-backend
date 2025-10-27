package com.backend.servicesTests;

import com.backend.model.dtos.FacilityLocationDTO;
import com.backend.model.entities.Facility;
import com.backend.model.entities.Location;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.VendingMachine;
import com.backend.model.valueObjects.Latitude;
import com.backend.model.valueObjects.Longitude;
import com.backend.repositories.FacilityRepository;
import com.backend.services.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    private List<Facility> facilities;
    private Restaurant restaurant;
    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());
        restaurant.setName("La Bella Italia");
        restaurant.setDescription("Italian restaurant");

        Location restaurantLocation = new Location();
        restaurantLocation.setLatitude(new Latitude(52.2297));
        restaurantLocation.setLongitude(new Longitude(1.0122));
        restaurant.setLocation(restaurantLocation);

        vendingMachine = new VendingMachine();
        vendingMachine.setId(UUID.randomUUID());
        vendingMachine.setDescription("Snack vending machine");
        vendingMachine.setType(VendingMachine.Type.SNACKS);

        Location vmLocation = new Location();
        vmLocation.setLatitude(new Latitude(52.2310));
        vmLocation.setLongitude(new Longitude(21.0125));
        vendingMachine.setLocation(vmLocation);

        facilities = List.of(restaurant, vendingMachine);
    }

    @Test
    void shouldReturnAllFacilities() {
        when(facilityRepository.findAll()).thenReturn(facilities);

        List<FacilityLocationDTO> result = facilityService.getAllFacilities();

        assertThat(result).extracting("type")
                .containsExactlyInAnyOrder(FacilityLocationDTO.FacilityType.RESTAURANT, FacilityLocationDTO.FacilityType.VENDING_MACHINE);

        verify(facilityRepository).findAll();
    }

    @Test
    void shouldMapRestaurantCorrectly() {
        when(facilityRepository.findAll()).thenReturn(List.of(restaurant));

        List<FacilityLocationDTO> result = facilityService.getAllFacilities();

        FacilityLocationDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(restaurant.getId());
        assertThat(dto.getType()).isEqualTo(FacilityLocationDTO.FacilityType.RESTAURANT);
        assertThat(dto.getName()).isEqualTo("La Bella Italia");
        assertThat(dto.getLatitude()).isEqualTo(52.2297);
        assertThat(dto.getLongitude()).isEqualTo(1.0122);
    }
    @Test
    void shouldMapVendingMachineCorrectly() {
        when(facilityRepository.findAll()).thenReturn(List.of(vendingMachine));

        List<FacilityLocationDTO> result = facilityService.getAllFacilities();

        FacilityLocationDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(vendingMachine.getId());
        assertThat(dto.getType()).isEqualTo(FacilityLocationDTO.FacilityType.VENDING_MACHINE);
        assertThat(dto.getLatitude()).isEqualTo(52.2310);
        assertThat(dto.getLongitude()).isEqualTo(21.0125);
    }
}
