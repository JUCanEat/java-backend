package com.backend.services;

import com.backend.model.entities.User;
import com.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User getOrCreateUserFromJwt(Jwt jwt) {
        String userId = jwt.getSubject();

        return userRepository.findById(userId)
                .orElseGet(() -> createUserFromJwt(jwt));
    }

    private User createUserFromJwt(Jwt jwt) {
        User user = new User();
        user.setId(jwt.getSubject());
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){ //DEBUG ONLY DELETE FOR PRODUCTION!
        return userRepository.findAll();
    }

}