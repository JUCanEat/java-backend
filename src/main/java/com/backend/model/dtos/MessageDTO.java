package com.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private UUID id;
    private UUID restaurantId;
    private LocalDate date;
    private String imagePath;
    private String ownerId;

}