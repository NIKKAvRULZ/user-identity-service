package com.foodsystem.user_identity_service.service;

import com.foodsystem.user_identity_service.model.User;
import com.foodsystem.user_identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Logic for Registration
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }
        return userRepository.save(user);
    }

    // Logic for Login
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)); 
                // Note: In a real app, you'd use BCrypt here for security!
    }

    // Logic for Profile Retrieval (Used by Order Service)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}