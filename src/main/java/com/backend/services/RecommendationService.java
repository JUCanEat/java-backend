package com.backend.services;

import com.backend.model.dtos.RankedRestaurantDTO;
import com.backend.model.dtos.RestaurantListDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.Tag;
import com.backend.model.entities.User;
import com.backend.model.entities.UserPreference;
import com.backend.repositories.RestaurantRepository;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<RankedRestaurantDTO> getRankedRestaurants(String userId) {
        User user = findUserById(userId);

        List<Restaurant> restaurants =
                restaurantRepository.findAllRestaurantsWithTodayMenu();

        Set<Tag> include = new HashSet<>();
        Set<Tag> exclude = new HashSet<>();

        for (UserPreference p : user.getPreferences()) {
            if (p.getPreferenceType() == UserPreference.PreferenceType.INCLUDE) {
                include.add(p.getTag());
            } else {
                exclude.add(p.getTag());
            }
        }

        return restaurants.stream()
                .map(r -> new RankedRestaurantDTO(
                        new RestaurantListDTO(r),
                        calculateScore(r, include, exclude)
                ))
                .sorted(Comparator.comparingDouble(RankedRestaurantDTO::score).reversed())
                .toList();
    }

    private double calculateScore(Restaurant restaurant,
                                  Set<Tag> include,
                                  Set<Tag> exclude) {

        DailyMenu activeMenu = restaurant.getDailyMenus().stream()
                .filter(dm -> dm.getStatus() == DailyMenu.Status.ACTIVE)
                .findFirst()
                .orElse(null);

        if (activeMenu == null) {
            return -Double.MAX_VALUE;
        }

        List<Dish> dishes = restaurant.getTodayMenu().getDishes();

        if (dishes == null || dishes.isEmpty()) {
            return -10;
        }

        double score = 0;

        int totalDishes = dishes.size();
        int allergenHits = 0;
        boolean hasDietaryMatch = false;

        for (Dish dish : dishes) {

            Set<Tag> tags = dish.getTags();

            // -------------------------
            // ALLERGEN (ratio-based)
            // -------------------------
            boolean hasExcluded = false;

            for (Tag t : tags) {
                if (exclude.contains(t) && t.getTagType()== Tag.TagType.ALLERGEN) {
                    hasExcluded = true;
                    allergenHits++;
                    break;
                }
            }

            if (hasExcluded) {
                continue;
            }

            // -------------------------
            // DIETARY / INCLUDE bonus
            // -------------------------
            for (Tag t : tags) {
                if (include.contains(t) && t.getTagType() == Tag.TagType.DIETARY) {
                    score += 3;
                    hasDietaryMatch = true;
                }
            }

            // -------------------------
            // CUISINE/ALLERGEN bonus
            // -------------------------
            for (Tag t : tags) {
                if ((t.getTagType() == Tag.TagType.CUISINE || t.getTagType() == Tag.TagType.ALLERGEN )&& include.contains(t)) {
                    score += 2;
                }
            }
        }

        // -------------------------
        // ALLERGEN penalty (ratio-based)
        // -------------------------
        double allergenRatio = (double) allergenHits / totalDishes;
        double allergenPenalty = allergenRatio * 20;

        // -------------------------
        // DIETARY penalty (no match)
        // -------------------------
        double dietaryPenalty = 0;

        boolean hasAnyDietaryPreference = include.stream()
                .anyMatch(t -> t.getTagType() == Tag.TagType.DIETARY);

        if (hasAnyDietaryPreference && !hasDietaryMatch) {
            dietaryPenalty = 20;
        }

        // -------------------------
        // FINAL SCORE
        // -------------------------
        double finalScore = score
                - allergenPenalty
                - dietaryPenalty;

        return finalScore / Math.sqrt(totalDishes);
    }
}