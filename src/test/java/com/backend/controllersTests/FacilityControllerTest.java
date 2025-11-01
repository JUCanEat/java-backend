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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

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
        // Given
        FacilityLocationDTO restaurant = new FacilityLocationDTO(
                UUID.randomUUID(),
                FacilityLocationDTO.FacilityType.RESTAURANT,
                "La Bella Italia",
                52.2297,
                21.0122
        );

        FacilityLocationDTO vendingMachine = new FacilityLocationDTO(
                UUID.randomUUID(),
                FacilityLocationDTO.FacilityType.VENDING_MACHINE,
                null,
                52.2310,
                21.0125
        );

        List<FacilityLocationDTO> facilities = List.of(restaurant, vendingMachine);

        when(facilityService.getAllFacilities()).thenReturn(facilities);

        mockMvc.perform(get("/api/facilities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("RESTAURANT"))
                .andExpect(jsonPath("$[0].name").value("La Bella Italia"))
                .andExpect(jsonPath("$[0].latitude").value(52.2297))
                .andExpect(jsonPath("$[0].longitude").value(21.0122))
                .andExpect(jsonPath("$[1].type").value("VENDING_MACHINE"));

        verify(facilityService).getAllFacilities();
    }
}