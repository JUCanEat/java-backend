package com.backend.servicesTests;

import com.backend.services.ProdAIService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.retry.annotation.Retryable;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ProdAIServiceRetryConfigTest {

    @Test
    void parseMenuFromImageShouldRetryOnlyTransientAiExceptions() throws NoSuchMethodException {
        Method method = ProdAIService.class.getMethod("parseMenuFromImage", byte[].class);
        Retryable retryable = method.getAnnotation(Retryable.class);

        assertThat(retryable).isNotNull();
        assertThat(retryable.retryFor()).containsExactly(TransientAiException.class);
    }
}
