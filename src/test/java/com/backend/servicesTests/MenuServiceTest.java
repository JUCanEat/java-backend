package com.backend.servicesTests;

import com.backend.model.dtos.DailyMenuDTO;
import com.backend.model.dtos.DishDTO;
import com.backend.model.dtos.MessageDTO;
import com.backend.model.entities.DailyMenu;
import com.backend.model.entities.Dish;
import com.backend.model.entities.Restaurant;
import com.backend.model.entities.User;
import com.backend.repositories.DailyMenuRepository;
import com.backend.repositories.DishRepository;
import com.backend.repositories.RestaurantRepository;
import com.backend.services.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock private DailyMenuRepository dailyMenuRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private DishRepository dishRepository;

    @InjectMocks private MenuService menuService;

    private UUID restaurantId;
    private Restaurant restaurant;
    private DailyMenu dailyMenu;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setId(UUID.randomUUID());
        activeMenu.setRestaurant(restaurant);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);
        activeMenu.setDate(LocalDate.now());

        dailyMenu = activeMenu;
    }

    @Test
    void shouldReturnDailyMenu() {
        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE))
                .thenReturn(Optional.of(dailyMenu));

        DailyMenuDTO result = menuService.getDailyMenuByRestaurantId(restaurantId);

        assertThat(result).isNotNull();
        verify(dailyMenuRepository).findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE);
    }

    @Test
    void shouldThrowExceptionWhenMenuNotFound() {
        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getDailyMenuByRestaurantId(restaurantId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }


    @Test
    void shouldUploadMenuImageSuccessfully() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("test-image".getBytes());
        when(file.getOriginalFilename()).thenReturn("menu.jpg");
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.ACTIVE))
                .thenReturn(Optional.empty());
        when(dailyMenuRepository.save(any(DailyMenu.class)))
                .thenAnswer(invocation -> {
                    DailyMenu menu = invocation.getArgument(0);
                    menu.setId(UUID.randomUUID());
                    return menu;
                });

        menuService.uploadMenuImage(restaurantId, file, "owner123");

        verify(dailyMenuRepository, times(1)).save(any(DailyMenu.class));
        verify(rabbitTemplate, times(1))
                .convertAndSend(anyString(), anyString(), any(MessageDTO.class));
    }

    @Test
    void shouldThrowWhenRestaurantNotFoundOnUpload() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());
        MultipartFile file = mock(MultipartFile.class);

        assertThatThrownBy(() ->
                menuService.uploadMenuImage(restaurantId, file, "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void shouldThrowWhenUserIsNotOwner() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        User owner = new User();
        owner.setId("otherOwner");
        restaurant.setOwners(Set.of(owner));
        MultipartFile file = mock(MultipartFile.class);

        assertThatThrownBy(() ->
                menuService.uploadMenuImage(restaurantId, file, "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not the owner");
    }

    @Test
    void shouldReturnDailyMenuDraft() {
        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.DRAFT))
                .thenReturn(Optional.of(dailyMenu));

        DailyMenuDTO result = menuService.getDailyMenuDraftByRestaurantId(restaurantId);

        assertThat(result).isNotNull();
        verify(dailyMenuRepository).findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.DRAFT);
    }

    @Test
    void shouldThrowWhenDailyMenuDraftNotFound() {
        when(dailyMenuRepository.findByRestaurantIdAndStatus(restaurantId, DailyMenu.Status.DRAFT))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getDailyMenuDraftByRestaurantId(restaurantId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldUpdateAndApproveMenu() {
        UUID menuId = UUID.randomUUID();
        DailyMenu menu = new DailyMenu();
        menu.setId(menuId);
        menu.setStatus(DailyMenu.Status.DRAFT);
        menu.setRestaurant(restaurant);

        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("Pizza");
        dishDTO.setCategory("MAIN_COURSE");
        dishDTO.setCategory("MAIN_COURSE");
        dishDTO.setPrice(BigDecimal.valueOf(25.0));
        dishDTO.setAllergens(Set.of(Dish.Allergens.GLUTEN));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDishes(List.of(dishDTO));

        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        menuService.updateAndApproveMenu(menuId, request, "owner123");

        assertThat(menu.getStatus()).isEqualTo(DailyMenu.Status.ACTIVE);
        verify(dishRepository, times(1)).save(any(Dish.class));
        verify(dailyMenuRepository, times(1)).save(menu);
    }

    @Test
    void shouldThrowWhenMenuNotFoundOnUpdate() {
        UUID menuId = UUID.randomUUID();
        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.empty());
        DailyMenuDTO request = new DailyMenuDTO();

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(menuId, request, "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldThrowWhenNotOwnerOnUpdate() {
        UUID menuId = UUID.randomUUID();
        DailyMenu menu = new DailyMenu();
        menu.setRestaurant(restaurant);

        User owner = new User();
        owner.setId("otherOwner");
        restaurant.setOwners(Set.of(owner));
        menu.setStatus(DailyMenu.Status.DRAFT);

        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(menuId, new DailyMenuDTO(), "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not the owner");
    }

    @Test
    void shouldThrowWhenMenuNotProcessedOnUpdate() {
        UUID menuId = UUID.randomUUID();
        DailyMenu menu = new DailyMenu();
        menu.setRestaurant(restaurant);
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));
        menu.setStatus(DailyMenu.Status.PROCESSING);

        when(dailyMenuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(menuId, new DailyMenuDTO(), "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Menu not ready");
    }
}