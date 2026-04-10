package com.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MenuUploadResponseDTO {
    private UUID menuId;
    private String status;
}