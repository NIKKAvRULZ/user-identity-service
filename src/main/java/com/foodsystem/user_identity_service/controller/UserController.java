package com.foodsystem.user_identity_service.controller;

import com.foodsystem.user_identity_service.model.User;
import com.foodsystem.user_identity_service.service.UserService;
import com.foodsystem.user_identity_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") //
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOpt = userService.loginUser(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // Get User by ID (Updated to String)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) { // Changed Long to String
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete User (Updated to String)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) { // Changed Long to String
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Get Recent Order Status (Updated to String)
    @GetMapping("/{id}/order-status")
    public ResponseEntity<String> getOrderStatus(@PathVariable String id) { // Changed Long to String
        return ResponseEntity.ok(userService.getRecentOrderStatus(id));
    }

    // Get Daily Deals from Catalog Node
    @GetMapping("/deals")
    public ResponseEntity<String> getDeals() {
        return ResponseEntity.ok(userService.getCatalogDeals());
    }

    // Ping endpoint for keep-alive (cron-job.org)
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Service is awake!");
    }
}