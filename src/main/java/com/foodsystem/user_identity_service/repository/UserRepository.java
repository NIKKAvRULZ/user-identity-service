package com.foodsystem.user_identity_service.repository;

import com.foodsystem.user_identity_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query method to find a user by username
    Optional<User> findByUsername(String username);
    
    // Custom method to check if a user exists by email
    boolean existsByEmail(String email);
}
