package com.foodsystem.user_identity_service.service;

import com.foodsystem.user_identity_service.model.User;
import com.foodsystem.user_identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    // The URL is pulled from environment variables in application.yaml
    @Value("${services.catalog-node}")
    private String catalogUrl;

    // Integration Logic: Fetch data from the Catalog Service
    public String getCatalogDeals() {
        try {
            // Easiest way to communicate: GET request to teammate's endpoint 
            return restTemplate.getForObject(catalogUrl + "/api/deals/daily", String.class);
        } catch (Exception e) {
            // Graceful failure if teammate's Render service is "sleeping" 
            return "Unable to fetch daily deals from Catalog Service.";
        }
    }
    // Logic for Registration
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }
        return userRepository.save(user);
    }

    // Logic for Login
    public Optional<User> loginUser(String email, String password) {
    // Find the user by email
    Optional<User> user = userRepository.findByEmail(email);
    // Verify password (Note: In a real app, use BCrypt.checkpw here)
    if (user.isPresent() && user.get().getPassword().equals(password)) {
        return user;
    }
    return Optional.empty();
    
}

    // Logic for Profile Retrieval (Used by Order Service)
    @SuppressWarnings("null")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}