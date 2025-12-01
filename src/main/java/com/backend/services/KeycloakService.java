package com.backend.services;

import com.backend.model.dtos.KeycloakUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final WebClient.Builder webClientBuilder;

    public KeycloakUserDTO getUserInfo(String accessToken) {
        String url = String.format(
                "%s/realms/%s/protocol/openid-connect/userinfo",
                keycloakServerUrl,
                realm
        );

        return webClientBuilder.build()
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KeycloakUserDTO.class)
                .block();
    }
}