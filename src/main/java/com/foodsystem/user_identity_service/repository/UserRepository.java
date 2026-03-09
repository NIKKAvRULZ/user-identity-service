package com.foodsystem.user_identity_service.repository;

import com.foodsystem.user_identity_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// Change Long to String for the ID type
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}