package com.backend.controllersTests;

import com.backend.controllers.FacilityController;
import com.backend.filters.SaveUserFilter;
import com.backend.model.dtos.FacilityLocationDTO;
import com.backend.services.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacilityController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SaveUserFilter.class),
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;

    @Test
    void shouldReturnAllFacilities() throws Exception {
        List<FacilityLocationDTO> facilities = getAllFacilities();
        int numOfFacilities = facilities.size();

        when(facilityService.getAllFacilities()).thenReturn(facilities);

        mockMvc.perform(get("/api/facilities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(numOfFacilities)));

        for (int i = 0; i < numOfFacilities; i++) {
            mockMvc.perform(get("/api/facilities")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[" + i + "].type").value("" + facilities.get(i).getType()))
                    .andExpect(jsonPath("$[" + i + "].name").value(facilities.get(i).getName()))
                    .andExpect(jsonPath("$[" + i + "].latitude").value(facilities.get(i).getLatitude()))
                    .andExpect(jsonPath("$[" + i + "].longitude").value(facilities.get(i).getLongitude()));
        }

        verify(facilityService, times(numOfFacilities + 1)).getAllFacilities();
    }

    private List<FacilityLocationDTO> getAllFacilities() {
        FacilityLocationDTO restaurant1 = new FacilityLocationDTO(
                UUID.fromString("b2a5f4de-8f39-4e3e-a51e-8c527ce7e1a1"),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "Bistro Świetlica",
                50.030511999856294, 19.90718833360244
        );

        FacilityLocationDTO restaurant2 = new FacilityLocationDTO(
                UUID.fromString("e7c37f89-26b1-4cb5-9b43-6e2c15a0d9a8"),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "Bistro by Jelonek",
                50.030170829430276, 19.908160572569297
        );

        FacilityLocationDTO restaurant3 = new FacilityLocationDTO(
                UUID.fromString("4a2a13c4-34be-4bef-82c3-a879c91bba5b"),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "Bistro4mat",
                50.027354360288484, 19.900980407813968
        );

        FacilityLocationDTO restaurant4 = new FacilityLocationDTO(
                UUID.fromString("76e4647b-cb56-460a-9f30-a6b3482fb93a"),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "Bistro 11",
                50.03000205209573, 19.905736283744663
        );

        FacilityLocationDTO restaurant5 = new FacilityLocationDTO(
                UUID.fromString("1cbbef85-cac6-4eb4-afd5-feb2f88e5866"),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "Neon Bistro",
                50.02902509906889, 19.907067338269396
        );

        FacilityLocationDTO vendingMachine1 = new FacilityLocationDTO(
                UUID.fromString("c3b6a5ef-9f40-5d4f-b62f-9d638df8f2b2"),
                FacilityLocationDTO.FacilityType.VENDING_MACHINE,
                null,
                50.030558894832836, 19.907430036655054
        );

        FacilityLocationDTO vendingMachine2 = new FacilityLocationDTO(
                UUID.fromString("e5d8c7f1-1b62-7f6f-d84f-1f850ff0f4d4"),
                FacilityLocationDTO.FacilityType.VENDING_MACHINE,
                null,
                50.03033705913868, 19.908204149300445
        );

        FacilityLocationDTO vendingMachine3 = new FacilityLocationDTO(
                UUID.fromString("d4c7b6f0-0a51-6e5f-c73f-0e749ef9f3c3"),
                FacilityLocationDTO.FacilityType.VENDING_MACHINE,
                null,
                50.02611453383066, 19.900576194001417
        );

        return List.of(restaurant1, restaurant2, restaurant3, restaurant4, restaurant5, vendingMachine1, vendingMachine2, vendingMachine3);
    }
}