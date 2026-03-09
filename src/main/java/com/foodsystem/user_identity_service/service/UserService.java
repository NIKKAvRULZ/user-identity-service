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

    // External Service URLs from environment variables in application.yaml
    @Value("${services.catalog-node}")
    private String catalogUrl;

    @Value("${services.order-node}")
    private String orderUrl;

    // --- Integration Logic: Catalog Service ---
    public String getCatalogDeals() {
        try {
            // GET request to teammate's Catalog endpoint
            return restTemplate.getForObject(catalogUrl + "/api/deals/daily", String.class);
        } catch (Exception e) {
            // Graceful failure if Catalog Node is sleeping
            return "Unable to fetch daily deals from Catalog Service.";
        }
    }

    // --- NEW Integration Logic: Order Service ---
    public String getRecentOrderStatus(Long userId) {
        try {
            // GET request to teammate's Order endpoint
            return restTemplate.getForObject(orderUrl + "/api/orders/status/" + userId, String.class);
        } catch (Exception e) {
            // Graceful failure if Order Node is sleeping
            return "Unable to fetch order status from Order Service.";
        }
    }

    // --- Core Identity Logic ---
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    @SuppressWarnings("null")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}