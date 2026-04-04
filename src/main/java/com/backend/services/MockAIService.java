package com.backend.services;

import com.backend.model.entities.Dish;
import com.backend.repositories.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "aiservice.mock-implementation-active", havingValue = "true")
public class MockAIService implements MenuAIService {
    private final DishRepository dishRepository;

    public List<Dish> parseMenuFromImage(byte[] imageBytes) {
        return dishRepository.findAll();
    }
}
