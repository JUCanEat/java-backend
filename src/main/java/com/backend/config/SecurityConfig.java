package com.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        @Bean
        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
            http.cors(Customizer.withDefaults()).csrf(c -> c.disable())
                    .authorizeHttpRequests((a) -> a
                                    .requestMatchers(HttpMethod.POST, "/api/restaurants").hasRole("restaurant_owner")
                                    .requestMatchers(HttpMethod.PUT, "/api/restaurants/**").hasRole("restaurant_owner")
                                    .requestMatchers(HttpMethod.GET, "/api/restaurants/**/draft").hasRole("restaurant_owner")
                                    .requestMatchers(HttpMethod.GET, "/api/menus/events").hasRole("restaurant_owner")
                                    .requestMatchers(HttpMethod.GET, "/api/users").permitAll() //DEBUG ONLY!
                                   // .anyRequest().authenticated());
                                    .anyRequest().permitAll());
            http.oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
            return http.build();
        }

}