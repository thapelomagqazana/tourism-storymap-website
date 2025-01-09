package com.tourism.tourism_backend.repositories;

import com.tourism.tourism_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email.
     * 
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not
     */
    Optional<User> findByEmail(String email);
}