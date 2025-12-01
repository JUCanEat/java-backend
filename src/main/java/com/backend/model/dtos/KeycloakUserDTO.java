package com.backend.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeycloakUserDTO {
    @JsonProperty("sub")
    private String id;
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;
    private String name;
}
