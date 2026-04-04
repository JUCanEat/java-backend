package com.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class DeepLResponse {
    private List<Translation> translations;

    @Data
    public static class Translation {
        private String text;
    }
}