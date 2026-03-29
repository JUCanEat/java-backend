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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
                when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, LocalDate.now()))
                .thenReturn(Optional.of(dailyMenu));

        DailyMenuDTO result = menuService.getDailyMenuByRestaurantId(restaurantId);

        assertThat(result).isNotNull();
                verify(dailyMenuRepository).findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, LocalDate.now());
    }

    @Test
    void shouldThrowExceptionWhenMenuNotFound() {
                when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, LocalDate.now()))
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


        when(dailyMenuRepository.save(any(DailyMenu.class)))
                .thenAnswer(invocation -> {
                    DailyMenu menu = invocation.getArgument(0);
                    menu.setId(UUID.randomUUID());
                    return menu;
                });

        menuService.uploadMenuImage(restaurantId, file, owner.getId());

        verify(dailyMenuRepository, times(1)).save(any(DailyMenu.class));
        verify(dailyMenuRepository, times(1))
                .deleteByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, LocalDate.now());
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
    void shouldReplaceMenuDraft() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("string-imitating-test-image".getBytes());
        when(file.getOriginalFilename()).thenReturn("menu.jpg");
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(dailyMenuRepository.save(any())).thenReturn(dailyMenu);

        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        dailyMenu.setStatus(DailyMenu.Status.DRAFT);
        menuService.uploadMenuImage(restaurantId, file, owner.getId());

                verify(dailyMenuRepository, times(1)).deleteByRestaurantIdAndStatusAndDate(any(), eq(DailyMenu.Status.DRAFT), eq(LocalDate.now()));
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
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, LocalDate.now()))
                .thenReturn(Optional.empty());

        DailyMenu draft = new DailyMenu();
        draft.setId(UUID.randomUUID());
        draft.setRestaurant(restaurant);
        draft.setStatus(DailyMenu.Status.DRAFT);

        draft.setDate(LocalDate.now());

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, LocalDate.now()))
                .thenReturn(Optional.of(draft));

        DailyMenu active = new DailyMenu();
        active.setId(UUID.randomUUID());
        active.setStatus(DailyMenu.Status.ACTIVE);
        active.setDate(LocalDate.now());

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, LocalDate.now()))
                .thenReturn(Optional.of(active));

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("Pizza");
        dishDTO.setCategory("MAIN_COURSE");
        dishDTO.setPrice(BigDecimal.valueOf(25.0));
        dishDTO.setAllergens(Set.of(Dish.Allergens.GLUTEN));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDate(LocalDate.now());
        request.setDishes(List.of(dishDTO));

        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        menuService.updateAndApproveMenu(restaurantId, request, "owner123");

        assertThat(draft.getStatus()).isEqualTo(DailyMenu.Status.ACTIVE);
                assertThat(active.getStatus()).isEqualTo(DailyMenu.Status.ACTIVE);

        verify(dishRepository, times(1)).save(any(Dish.class));
                verify(dailyMenuRepository, times(1)).save(any(DailyMenu.class));
    }

    @Test
    void shouldCreateMenu() {
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, LocalDate.now()))
                .thenReturn(Optional.empty());


        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, LocalDate.now()))
                .thenReturn(Optional.empty());

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, LocalDate.now()))
                .thenReturn(Optional.empty());

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("Pizza");
        dishDTO.setCategory("MAIN_COURSE");
        dishDTO.setPrice(BigDecimal.valueOf(25.0));
        dishDTO.setAllergens(Set.of(Dish.Allergens.GLUTEN));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDate(LocalDate.now());
        request.setDishes(List.of(dishDTO));

        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        menuService.updateAndApproveMenu(restaurantId, request, "owner123");

        verify(dishRepository, times(1)).save(any(Dish.class));
                verify(dailyMenuRepository, times(1)).save(any(DailyMenu.class));
    }

    @Test
    void shouldThrowWhenRestaurantNotFoundOnUpdate() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(restaurantId, new DailyMenuDTO(), "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void shouldThrowWhenNotOwnerOnUpdate() {

        User owner = new User();
        owner.setId("otherOwner");
        restaurant.setOwners(Set.of(owner));

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(restaurantId, new DailyMenuDTO(), "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not the owner");
    }

    @Test
    void shouldThrowWhenMenuIsProcessing() {
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        DailyMenu processing = new DailyMenu();
        processing.setStatus(DailyMenu.Status.PROCESSING);

        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, LocalDate.now()))
                .thenReturn(Optional.of(processing));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDate(LocalDate.now());

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(restaurantId, request, "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Menu is currently being processed");
    }

    @Test
    void shouldScheduleFuturePublishedMenu() {
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        LocalDate futureDate = LocalDate.now().plusDays(3);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, futureDate))
                .thenReturn(Optional.empty());
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.DRAFT, futureDate))
                .thenReturn(Optional.empty());
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, futureDate))
                .thenReturn(Optional.empty());

        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("Soup");
        dishDTO.setCategory("SOUP");
        dishDTO.setPrice(BigDecimal.valueOf(19.0));
        dishDTO.setAllergens(Set.of());

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDate(futureDate);
        request.setDishes(List.of(dishDTO));

        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        menuService.updateAndApproveMenu(restaurantId, request, "owner123");

        ArgumentCaptor<DailyMenu> captor = ArgumentCaptor.forClass(DailyMenu.class);
        verify(dailyMenuRepository).save(captor.capture());
        assertThat(captor.getValue().getDate()).isEqualTo(futureDate);
        assertThat(captor.getValue().getStatus()).isEqualTo(DailyMenu.Status.ACTIVE);
    }

    @Test
    void shouldAllowEditingPublishedMenuById() {
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        LocalDate targetDate = LocalDate.now().plusDays(1);
        UUID menuId = UUID.randomUUID();

        DailyMenu activeMenu = new DailyMenu();
        activeMenu.setId(menuId);
        activeMenu.setRestaurant(restaurant);
        activeMenu.setStatus(DailyMenu.Status.ACTIVE);
        activeMenu.setDate(targetDate);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.PROCESSING, targetDate))
                .thenReturn(Optional.empty());
        when(dailyMenuRepository.findByIdAndRestaurantId(menuId, restaurantId)).thenReturn(Optional.of(activeMenu));
        when(dailyMenuRepository.findByRestaurantIdAndStatusAndDate(restaurantId, DailyMenu.Status.ACTIVE, targetDate))
                .thenReturn(Optional.of(activeMenu));

        DishDTO dishDTO = new DishDTO();
        dishDTO.setName("Edited Pizza");
        dishDTO.setCategory("MAIN_COURSE");
        dishDTO.setPrice(BigDecimal.valueOf(31.0));
        dishDTO.setAllergens(Set.of(Dish.Allergens.GLUTEN));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setId(menuId);
        request.setDate(targetDate);
        request.setDishes(List.of(dishDTO));

        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));

        menuService.updateAndApproveMenu(restaurantId, request, "owner123");

        verify(dailyMenuRepository, never()).save(argThat(menu -> menu.getStatus() == DailyMenu.Status.INACTIVE));
        verify(dailyMenuRepository).save(activeMenu);
    }

    @Test
    void shouldThrowWhenSchedulingMenuInPast() {
        User owner = new User();
        owner.setId("owner123");
        restaurant.setOwners(Set.of(owner));

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));

        DailyMenuDTO request = new DailyMenuDTO();
        request.setDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() ->
                menuService.updateAndApproveMenu(restaurantId, request, "owner123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("past");
    }

        @Test
        void shouldThrowWhenDateMissing() {
                User owner = new User();
                owner.setId("owner123");
                restaurant.setOwners(Set.of(owner));

                when(restaurantRepository.findById(restaurantId))
                                .thenReturn(Optional.of(restaurant));

                DailyMenuDTO request = new DailyMenuDTO();
                request.setDishes(List.of());

                assertThatThrownBy(() ->
                                menuService.updateAndApproveMenu(restaurantId, request, "owner123"))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("date is required");
        }
}