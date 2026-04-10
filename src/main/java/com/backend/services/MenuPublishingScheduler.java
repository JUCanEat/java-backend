package com.backend.services;

import com.backend.model.entities.DailyMenu;
import com.backend.repositories.DailyMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuPublishingScheduler {

    private final DailyMenuRepository dailyMenuRepository;

    @Scheduled(cron = "${menus.scheduler.cron:0 5 * * * *}", zone = "${menus.scheduler.zone:Europe/Warsaw}")
    @Transactional
    public void publishScheduledMenusForToday() {
        LocalDate today = LocalDate.now();

        List<DailyMenu> scheduledMenus = dailyMenuRepository
                .findAllByStatusAndDate(DailyMenu.Status.SCHEDULED, today);

        for (DailyMenu scheduledMenu : scheduledMenus) {
            Optional<DailyMenu> activeMenu = dailyMenuRepository.findByRestaurantIdAndStatusAndDate(
                    scheduledMenu.getRestaurant().getId(),
                    DailyMenu.Status.ACTIVE,
                    today
            );

            if (activeMenu.isPresent() && !activeMenu.get().getId().equals(scheduledMenu.getId())) {
                activeMenu.get().setStatus(DailyMenu.Status.INACTIVE);
                dailyMenuRepository.save(activeMenu.get());
            }

            scheduledMenu.setStatus(DailyMenu.Status.ACTIVE);
            dailyMenuRepository.save(scheduledMenu);

            log.info("Activated scheduled menu {} for restaurant {}", scheduledMenu.getId(), scheduledMenu.getRestaurant().getId());
        }
    }
}
