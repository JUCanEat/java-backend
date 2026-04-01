package com.backend.model;

import com.backend.model.dtos.MessageDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.DailyMenuRepository;
import com.backend.services.MenuAIService;
import com.backend.services.SseEmitterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuProcessingListenerTest {

    @Mock
    private DailyMenuRepository dailyMenuRepository;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private MenuAIService menuAIService;

    @InjectMocks
    private MenuProcessingListener menuProcessingListener;

    @Test
    void shouldCreateDraftAfterSuccessfulAiProcessing() {
        UUID menuId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        DailyMenu menu = new DailyMenu();
        menu.setId(menuId);
        menu.setRestaurant(restaurant);
        menu.setDate(LocalDate.now());
        menu.setStatus(DailyMenu.Status.PROCESSING);

        Dish parsedDish = new Dish();
        parsedDish.setName("Tomato soup");
        parsedDish.setCategory(Dish.Category.SOUP);
        parsedDish.setAllergens(Set.of());

        MessageDTO message = new MessageDTO(menuId, restaurantId, LocalDate.now(), new byte[] {1, 2, 3}, "menu.jpg", "owner-1");

        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuAIService.parseMenuFromImage(message.getImageData())).thenReturn(List.of(parsedDish));

        menuProcessingListener.processMenu(message);

        assertThat(menu.getStatus()).isEqualTo(DailyMenu.Status.DRAFT);
        assertThat(menu.getDishes()).hasSize(1);
        verify(dailyMenuRepository).save(menu);
        verify(sseEmitterService).sendEvent("owner-1", menuId);
    }

    @Test
    void shouldMarkMenuAsFailedWhenAiReturnsNoItems() {
        UUID menuId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();

        DailyMenu menu = new DailyMenu();
        menu.setId(menuId);
        menu.setDate(LocalDate.now());
        menu.setStatus(DailyMenu.Status.PROCESSING);

        MessageDTO message = new MessageDTO(menuId, restaurantId, LocalDate.now(), new byte[] {1}, "menu.jpg", "owner-1");

        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuAIService.parseMenuFromImage(message.getImageData())).thenReturn(List.of());

        menuProcessingListener.processMenu(message);

        assertThat(menu.getStatus()).isEqualTo(DailyMenu.Status.FAILED);
        verify(dailyMenuRepository).save(menu);
        verify(sseEmitterService, never()).sendEvent(any(), any());
    }
}
