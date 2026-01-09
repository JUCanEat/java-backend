package com.backend.servicesTests;

import com.backend.model.dtos.AIDishDTO;
import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.DishDTO;
import com.backend.model.dtos.MessageDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.model.valueObjects.Price;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.DishRepository;
import com.backend.repositories.RestaurantRepository;
import com.backend.services.MenuAIService;
import com.backend.services.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuAIServiceTest {
    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec promptSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private MenuAIService menuAIService;

//    @Captor
//    private ArgumentCaptor<Dish> dishCaptor;

    private byte[] testImageBytes;

    @BeforeEach
    void setUp() {
        testImageBytes = "fake-image-data".getBytes();

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(promptSpec);
        when(promptSpec.user(any(Consumer.class))).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void parseMenuFromImage_shouldReturnEmptyList_whenNoImageProvided() {
        byte[] emptyImage = new byte[0];
        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenReturn(Collections.emptyList());

        List<Dish> result = menuAIService.parseMenuFromImage(emptyImage);


        assertThat(result).isEmpty();
    }

    @Test
    void parseMenuFromImage_shouldParseSingleDish_successfully() {
        AIDishDTO dishDTO = new AIDishDTO();
        dishDTO.setName("Rosół z makaronem");
        dishDTO.setCategory("SOUP");
        dishDTO.setPrice(BigDecimal.valueOf(12.50));
        dishDTO.setAllergens(Set.of(Dish.Allergens.MEAT, Dish.Allergens.GLUTEN));

        Dish savedDish = createDishFromDTO(dishDTO);

        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenReturn(Collections.singletonList(dishDTO));
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);



        List<Dish> result = menuAIService.parseMenuFromImage(testImageBytes);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Rosół z makaronem");
        assertThat(result.get(0).getCategory()).isEqualTo(Dish.Category.SOUP);
        assertThat(result.get(0).getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(12.50));
        assertThat(result.get(0).getAllergens())
                .containsOnly(Dish.Allergens.MEAT, Dish.Allergens.GLUTEN);

        verify(dishRepository, times(1)).save(any(Dish.class));
    }

    @Test
    void parseMenuFromImage_shouldParseMultipleDishes_successfully() {
        AIDishDTO dish1 = createAIDishDTO("Schabowy panierowany", Dish.Category.MAIN_COURSE,
                BigDecimal.valueOf(25.00), Set.of(Dish.Allergens.MEAT, Dish.Allergens.GLUTEN));
        AIDishDTO dish2 = createAIDishDTO("Spaghetti carbonara", Dish.Category.MAIN_COURSE,
                BigDecimal.valueOf(22.50),Set.of(Dish.Allergens.MEAT, Dish.Allergens.GLUTEN));
        AIDishDTO dish3 = createAIDishDTO("Zupa krem z brokułów", Dish.Category.SOUP,
                BigDecimal.valueOf(10.00), Set.of(Dish.Allergens.LACTOSE));

        List<AIDishDTO> dishDTOs = Arrays.asList(dish1, dish2, dish3);

        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenReturn(dishDTOs);
        when(dishRepository.save(any(Dish.class)))
                .thenAnswer(invocation -> {
                    Dish dish = invocation.getArgument(0);
                    return dish;
                });

        List<Dish> result = menuAIService.parseMenuFromImage(testImageBytes);

        assertThat(result).hasSize(3);
        assertThat(result).extracting("name")
                .containsExactly("Schabowy panierowany", "Spaghetti carbonara", "Zupa krem z brokułów");

        verify(dishRepository, times(3)).save(any(Dish.class));
    }

    @Test
    void parseMenuFromImage_shouldRetryAfterParsingError_andSucceed() {
        AIDishDTO dish = createAIDishDTO(
                "Schabowy panierowany",
                Dish.Category.MAIN_COURSE,
                BigDecimal.valueOf(25.00),
                Set.of(Dish.Allergens.MEAT)
        );

        List<AIDishDTO> successResponse = List.of(dish);

        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("JSON parse error"))
                .thenReturn(successResponse);

        when(dishRepository.save(any(Dish.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Dish> result = menuAIService.parseMenuFromImage(testImageBytes);


        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Schabowy panierowany");

        verify(callResponseSpec, times(2))
                .entity(any(ParameterizedTypeReference.class));

        verify(dishRepository, times(1)).save(any(Dish.class));
    }

    @Test
    void parseMenuFromImage_shouldReturnEmptyList_afterMaxRetriesExceeded() {

        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Invalid JSON"));

        List<Dish> result = menuAIService.parseMenuFromImage(testImageBytes);

        assertThat(result).isEmpty();

        verify(callResponseSpec, times(5))
                .entity(any(ParameterizedTypeReference.class));

        verifyNoInteractions(dishRepository);
    }

    @Test
    void parseMenuFromImage_shouldRetryWhenEmptyListReturned_andThenSucceed() {
        AIDishDTO dish = createAIDishDTO(
                "Rosół",
                Dish.Category.SOUP,
                BigDecimal.valueOf(12.00),
                Set.of(Dish.Allergens.MEAT)
        );

        when(callResponseSpec.entity(any(ParameterizedTypeReference.class)))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(dish));

        when(dishRepository.save(any(Dish.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Dish> result = menuAIService.parseMenuFromImage(testImageBytes);

        assertThat(result).hasSize(1);
    }


    private Dish createDishFromDTO(AIDishDTO dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setCategory(Dish.Category.valueOf(dto.getCategory()));
        dish.setPrice(new Price(dto.getPrice(), "PLN"));
        dish.setAllergens(dto.getAllergens());
        return dish;
    }

    private AIDishDTO createAIDishDTO(String name, Dish.Category category, BigDecimal price, Set<Dish.Allergens> allergens) {
        AIDishDTO dto = new AIDishDTO();
        dto.setName(name);
        dto.setCategory(category.toString());
        dto.setPrice(price);
        dto.setAllergens(allergens);
        return dto;
    }

}