package com.backend.servicesTests;

import com.backend.model.dtos.RankedRestaurantDTO;
import com.backend.model.entities.*;
import com.backend.repositories.RestaurantRepository;
import com.backend.repositories.UserRepository;
import com.backend.services.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
    }

    @Test
    void shouldReturnVeryLowScoreWhenNoActiveMenu() {

        Restaurant restaurant = new Restaurant();
        restaurant.setName("No active menu");

        DailyMenu inactive = new DailyMenu();
        inactive.setStatus(DailyMenu.Status.INACTIVE);

        restaurant.setDailyMenus(Set.of(inactive));

        when(userRepository.findById(user.getId()))
                .thenReturn(java.util.Optional.of(user));

        when(restaurantRepository.findAllRestaurantsWithTodayMenu())
                .thenReturn(List.of(restaurant));

        List<RankedRestaurantDTO> result =
                recommendationService.getRankedRestaurants(user.getId());

        assertEquals(1, result.size());

        assertEquals(-Double.MAX_VALUE, result.getFirst().score());
    }

    @Test
    void shouldGiveHigherScoreForMatchingDietaryTags() {

        Tag vegan = new Tag();
        vegan.setValue(Tag.TagValue.VEGAN);

        UserPreference pref = new UserPreference();
        pref.setPreferenceType(UserPreference.PreferenceType.INCLUDE);
        pref.setTag(vegan);

        user.setPreferences(List.of(pref));

        Dish dish = new Dish();
        dish.setTags(Set.of(vegan));

        DailyMenu menu = new DailyMenu();
        menu.setStatus(DailyMenu.Status.ACTIVE);
        menu.setDishes(List.of(dish));

        Restaurant restaurant = new Restaurant();
        restaurant.setDailyMenus(Set.of(menu));

        when(userRepository.findById(user.getId()))
                .thenReturn(java.util.Optional.of(user));

        when(restaurantRepository.findAllRestaurantsWithTodayMenu())
                .thenReturn(List.of(restaurant));

        List<RankedRestaurantDTO> result =
                recommendationService.getRankedRestaurants(user.getId());

        assertTrue(result.getFirst().score() > 0);
    }

    @Test
    void shouldPenalizeExcludedTags() {

        Tag nuts = new Tag();
        nuts.setValue(Tag.TagValue.NUTS);

        UserPreference pref = new UserPreference();
        pref.setPreferenceType(UserPreference.PreferenceType.EXCLUDE);
        pref.setTag(nuts);

        user.setPreferences(List.of(pref));

        Dish dish = new Dish();
        dish.setTags(Set.of(nuts));

        DailyMenu menu = new DailyMenu();
        menu.setStatus(DailyMenu.Status.ACTIVE);
        menu.setDishes(List.of(dish));

        Restaurant restaurant = new Restaurant();
        restaurant.setDailyMenus(Set.of(menu));

        when(userRepository.findById(user.getId()))
                .thenReturn(java.util.Optional.of(user));

        when(restaurantRepository.findAllRestaurantsWithTodayMenu())
                .thenReturn(List.of(restaurant));

        List<RankedRestaurantDTO> result =
                recommendationService.getRankedRestaurants(user.getId());

        assertTrue(result.getFirst().score() < 0);
    }

    @Test
    void shouldSortRestaurantsByScoreDescending() {

        Tag vegan = new Tag();
        vegan.setValue(Tag.TagValue.VEGAN);

        UserPreference pref = new UserPreference();
        pref.setPreferenceType(UserPreference.PreferenceType.INCLUDE);
        pref.setTag(vegan);

        user.setPreferences(List.of(pref));

        Dish matchingDish = new Dish();
        matchingDish.setTags(Set.of(vegan));

        Dish nonMatchingDish = new Dish();
        nonMatchingDish.setTags(Set.of());

        DailyMenu active1 = new DailyMenu();
        active1.setStatus(DailyMenu.Status.ACTIVE);
        active1.setDishes(List.of(matchingDish));

        DailyMenu active2 = new DailyMenu();
        active2.setStatus(DailyMenu.Status.ACTIVE);
        active2.setDishes(List.of(nonMatchingDish));

        Restaurant better = new Restaurant();
        better.setName("Better");
        better.setDailyMenus(Set.of(active1));

        Restaurant worse = new Restaurant();
        worse.setName("Worse");
        worse.setDailyMenus(Set.of(active2));

        when(userRepository.findById(user.getId()))
                .thenReturn(java.util.Optional.of(user));

        when(restaurantRepository.findAllRestaurantsWithTodayMenu())
                .thenReturn(List.of(worse, better));

        List<RankedRestaurantDTO> result =
                recommendationService.getRankedRestaurants(user.getId());

        assertEquals("Better",
                result.get(0).restaurant().getName());
    }
}