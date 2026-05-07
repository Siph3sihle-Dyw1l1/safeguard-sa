package com.safeguardsa.repositories;

import com.safeguardsa.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author ntsak
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // --- Used by Spring Security for login ---
    Optional<AppUser> findByUsername(String username);

    // --- Check for duplicate username on registration ---
    boolean existsByUsername(String username);

    // --- Check for duplicate email ---
    boolean existsByEmail(String email);

    // --- Find by role (e.g. list all ADMINs) ---
    java.util.List<AppUser> findByRole(String role);
}
