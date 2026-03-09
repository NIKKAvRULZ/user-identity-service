package com.foodsystem.user_identity_service.controller;

import com.foodsystem.user_identity_service.dto.LoginRequest;
import com.foodsystem.user_identity_service.model.User;
import com.foodsystem.user_identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Identity & Integration", description = "Endpoints for Auth and Inter-service Communication")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user node")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and fetch integrated network deals")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (user.isPresent()) {
            // Fetch live data from the Catalog Service (Inter-service Communication)
            String dailyDeals = userService.getCatalogDeals();

            // Create a composite response to include integrated data
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get().getId());
            response.put("username", user.get().getUsername());
            response.put("email", user.get().getEmail());
            response.put("deliveryAddress", user.get().getDeliveryAddress());
            response.put("recommendedDeals", dailyDeals); // Data from Catalog Node
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Identity-Service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile (Used by Order Service for Delivery Address)")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/network-deals")
    @Operation(summary = "INTEGRATION: Manual fetch of deals from Catalog Microservice")
    public ResponseEntity<String> getNetworkDeals() {
        return ResponseEntity.ok(userService.getCatalogDeals());
    }

    @GetMapping("/order-status/{id}")
    @Operation(summary = "INTEGRATION: Fetch live status from Order Microservice")
    public ResponseEntity<String> getLiveOrderStatus(@PathVariable Long id) {
        // This demonstrates the secondary handshake with the Order Node
        return ResponseEntity.ok(userService.getRecentOrderStatus(id));
    }
}