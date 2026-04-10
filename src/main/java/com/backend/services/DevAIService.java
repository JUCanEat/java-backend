package com.backend.services;

import com.backend.model.dtos.AIDishDTO;
import com.backend.model.entities.Dish;
import com.backend.model.entities.User;
import com.backend.repositories.DishRepository;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"dev"})
public class DevAIService implements MenuAIService {
    private final DishRepository dishRepository;

    public List<Dish> parseMenuFromImage(byte[] imageBytes) {
        return dishRepository.findAll();
    }
}
