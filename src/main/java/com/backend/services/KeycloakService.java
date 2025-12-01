package com.backend.services;

import com.backend.model.dtos.KeycloakUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

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
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                        "Invalid access token: " + body))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new ResponseStatusException(
                                        HttpStatus.SERVICE_UNAVAILABLE,
                                        "Keycloak is unavailable: " + body
                                ))
                )
                .bodyToMono(KeycloakUserDTO.class)
                .block();
    }
}