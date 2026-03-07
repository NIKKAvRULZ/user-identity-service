package com.foodsystem.user_identity_service.controller;

import com.foodsystem.user_identity_service.dto.LoginRequest;
import com.foodsystem.user_identity_service.model.User;
import com.foodsystem.user_identity_service.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    // Endpoint for Registration
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }
    
    // Endpoint for Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        // Use the service to find the user by email
        Optional<User> user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (user.isPresent()) {

            // fetch live data from the catalog service
            String dailyDeals = userService.getCatalogDeals();

            // Create a map to send both User info and Integrated data
            Map<String, Object> response = new HashMap<>();
            response.put("user", user.get());
            response.put("networkDeals", dailyDeals); // This shows inter-service communication            
            // Return 200 OK with the full User object for the frontend
            return ResponseEntity.ok(user.get());
        } else {
            // Return 401 Unauthorized if credentials don't match
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
    // Integration Endpoint: Order Management Service will call this
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/network-deals")
    public ResponseEntity<String> getNetworkDeals() {
        return ResponseEntity.ok(userService.getCatalogDeals());
    }
    

    
}