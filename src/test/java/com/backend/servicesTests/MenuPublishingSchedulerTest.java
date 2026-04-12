package com.backend.servicesTests;

import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.DailyMenuRepository;
import com.backend.services.MenuPublishingScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuPublishingSchedulerTest {

    @Mock
    private DailyMenuRepository dailyMenuRepository;

    @InjectMocks
    private MenuPublishingScheduler menuPublishingScheduler;

    @Test
    void shouldPromoteScheduledMenuToActive() {
        LocalDate today = LocalDate.now();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());

        DailyMenu scheduledMenu = new DailyMenu();
        scheduledMenu.setId(UUID.randomUUID());
        scheduledMenu.setRestaurant(restaurant);
        scheduledMenu.setDate(today);
        scheduledMenu.setStatus(DailyMenu.Status.SCHEDULED);

        when(dailyMenuRepository.findAllByStatusAndDate(DailyMenu.Status.SCHEDULED, today))
                .thenReturn(List.of(scheduledMenu));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurant.getId(), DailyMenu.Status.ACTIVE, today))
                .thenReturn(Optional.empty());

        menuPublishingScheduler.publishScheduledMenusForToday();

        verify(dailyMenuRepository).save(scheduledMenu);
    }

    @Test
    void shouldInactivateExistingActiveBeforePromotingScheduledMenu() {
        LocalDate today = LocalDate.now();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());

        DailyMenu scheduledMenu = new DailyMenu();
        scheduledMenu.setId(UUID.randomUUID());
        scheduledMenu.setRestaurant(restaurant);
        scheduledMenu.setDate(today);
        scheduledMenu.setStatus(DailyMenu.Status.SCHEDULED);

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setId(UUID.randomUUID());
        activeMenu.setRestaurant(restaurant);
        activeMenu.setDate(today);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);

        when(dailyMenuRepository.findAllByStatusAndDate(DailyMenu.Status.SCHEDULED, today))
                .thenReturn(List.of(scheduledMenu));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurant.getId(), DailyMenu.Status.ACTIVE, today))
                .thenReturn(Optional.of(activeMenu));

        menuPublishingScheduler.publishScheduledMenusForToday();

        verify(dailyMenuRepository).save(activeMenu);
        verify(dailyMenuRepository).save(scheduledMenu);
    }

    @Test
    void shouldNotInactivateWhenActiveAndScheduledAreSameMenu() {
        LocalDate today = LocalDate.now();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());

        DailyMenu scheduledMenu = new DailyMenu();
        UUID menuId = UUID.randomUUID();
        scheduledMenu.setId(menuId);
        scheduledMenu.setRestaurant(restaurant);
        scheduledMenu.setDate(today);
        scheduledMenu.setStatus(DailyMenu.Status.SCHEDULED);

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setId(menuId);
        activeMenu.setRestaurant(restaurant);
        activeMenu.setDate(today);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);

        when(dailyMenuRepository.findAllByStatusAndDate(DailyMenu.Status.SCHEDULED, today))
                .thenReturn(List.of(scheduledMenu));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurant.getId(), DailyMenu.Status.ACTIVE, today))
                .thenReturn(Optional.of(activeMenu));

        menuPublishingScheduler.publishScheduledMenusForToday();

        verify(dailyMenuRepository, never()).save(activeMenu);
        verify(dailyMenuRepository).save(scheduledMenu);
    }
}
