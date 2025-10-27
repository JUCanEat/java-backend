package com.backend.repositoriesTests;

import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Restaurant;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MenuRepositoryTest {

    @Autowired
    private DailyMenuRepository dailyMenuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindActiveMenuByRestaurantId() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test");
        restaurant = restaurantRepository.save(restaurant);

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setRestaurant(restaurant);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);
        dailyMenuRepository.save(activeMenu);

        DailyMenu inactiveMenu = new DailyMenu();
        inactiveMenu.setRestaurant(restaurant);
        inactiveMenu.setStatus(DailyMenu.Status.INACTIVE);
        dailyMenuRepository.save(inactiveMenu);

        inactiveMenu = new DailyMenu();
        inactiveMenu.setRestaurant(restaurant);
        inactiveMenu.setStatus(DailyMenu.Status.INACTIVE);
        dailyMenuRepository.save(inactiveMenu);

        entityManager.flush();
        entityManager.clear();

        Optional<DailyMenu> result = dailyMenuRepository
                .findByRestaurantIdAndStatus(restaurant.getId(), DailyMenu.Status.ACTIVE);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(DailyMenu.Status.ACTIVE);
    }
}