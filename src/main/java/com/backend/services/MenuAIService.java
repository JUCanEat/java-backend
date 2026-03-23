package com.backend.services;

import com.backend.model.entities.Dish;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface MenuAIService {
    List<Dish> parseMenuFromImage(byte[] imageBytes);
}


