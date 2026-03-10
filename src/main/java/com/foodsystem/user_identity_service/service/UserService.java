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

    @Value("${services.notification-node}")
    private String notificationUrl;

    @Value("${services.payment-node}")
    private String paymentUrl;

    // --- Integration Logic: Catalog Service ---
    public String getCatalogDeals() {
        try {
            // GET request to teammate's Catalog endpoint
            String cleanCatalogUrl = catalogUrl.endsWith("/") ? catalogUrl.substring(0, catalogUrl.length() - 1)
                    : catalogUrl;
            return restTemplate.getForObject(cleanCatalogUrl + "/menu/items", String.class);
        } catch (Exception e) {
            // Graceful failure if Catalog Node is sleeping
            return "Unable to fetch daily deals from Catalog Service.";
        }
    }

    // --- Integration Logic: Order Service ---
    public String getUserOrders(String userId) {
        try {
            String cleanOrderUrl = orderUrl.endsWith("/") ? orderUrl.substring(0, orderUrl.length() - 1) : orderUrl;
            String response = restTemplate.getForObject(cleanOrderUrl + "/orders", String.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.List<java.util.Map<String, Object>> allOrders = mapper.readValue(
                    response,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {
                    });

            java.util.List<java.util.Map<String, Object>> userOrders = allOrders.stream()
                    .filter(order -> userId.equals(String.valueOf(order.get("userId"))))
                    .collect(java.util.stream.Collectors.toList());

            return mapper.writeValueAsString(userOrders);
        } catch (Exception e) {
            System.err.println("Failed to fetch/filter orders: " + e.getMessage());
            return "[]";
        }
    }

    // --- Integration Logic: Payment Service (Updated to String ID) ---
    public String getRecentOrderStatus(String userId) { // Changed Long to String
        try {
            // GET request to teammate's Payment endpoint to retrieve user payments
            String cleanPaymentUrl = paymentUrl.endsWith("/") ? paymentUrl.substring(0, paymentUrl.length() - 1)
                    : paymentUrl;
            return restTemplate.getForObject(cleanPaymentUrl + "/api/payments/user/" + userId, String.class);
        } catch (Exception e) {
            // Graceful failure if Payment Node is sleeping
            return "Unable to fetch order status from Payment Service.";
        }
    }

    // --- Core Identity Logic ---
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }

        // 1. Save user to MongoDB Atlas
        User registeredUser = userRepository.save(user);

        // 2. Trigger Welcome Email via Notification Service
        try {
            // Log the final URL to verify there are no double slashes
            String baseUrl = notificationUrl.endsWith("/") ? notificationUrl.substring(0, notificationUrl.length() - 1)
                    : notificationUrl;
            String welcomeApiUrl = baseUrl + "/api/v1/notify/welcome/" + registeredUser.getId();
            System.out.println("DEBUG: Sending request to: " + welcomeApiUrl);

            // Explicitly use the String class for the response
            String response = restTemplate.getForObject(welcomeApiUrl, String.class);
            System.out.println("DEBUG: Notification Service Response: " + response);

        } catch (Exception e) {
            System.err.println("CRITICAL: Notification Handshake Failed!");
            System.err.println("Error Detail: " + e.getMessage());
            // This will print the full stack trace so we can see the exact error code
            e.printStackTrace();
        }

        return registeredUser;
    }

    // --- Delete Logic (Updated to String ID) ---
    public void deleteUserById(String userId) { // Changed Long to String
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            System.out.println("SUCCESS: User with ID " + userId + " deleted from MongoDB Atlas.");
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    // --- Fetch Logic (Updated to String ID) ---
    @SuppressWarnings("null")
    public Optional<User> getUserById(String id) { // Changed Long to String
        return userRepository.findById(id);
    }
}