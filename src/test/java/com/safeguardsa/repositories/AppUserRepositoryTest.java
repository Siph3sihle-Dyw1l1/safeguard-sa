package com.safeguardsa.repositories;

import com.safeguardsa.models.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository-layer tests for AppUserRepository (Member E — Security & QA).
 *
 * Uses @DataJpaTest — Spring Boot spins up an in-memory H2 database and
 * wires only the JPA slice. No full application context is loaded, so
 * these tests are fast and isolated.
 */
@DataJpaTest
@DisplayName("AppUserRepository Tests")
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AppUser savedUser;

    // -----------------------------------------------------------------------
    // Test data setup
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser();
        user.setUsername("johndoe");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setRole("USER");
        user.setEmail("johndoe@example.com");
        user.setCreatedAt(LocalDateTime.now());

        savedUser = entityManager.persistAndFlush(user);
    }

    // -----------------------------------------------------------------------
    // findByUsername
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findByUsername should return user when username exists")
    void testFindByUsername_found() {
        Optional<AppUser> result = appUserRepository.findByUsername("johndoe");

        assertTrue(result.isPresent(), "Expected user to be found");
        assertEquals("johndoe", result.get().getUsername());
    }

    @Test
    @DisplayName("findByUsername should return empty Optional when username does not exist")
    void testFindByUsername_notFound() {
        Optional<AppUser> result = appUserRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent(), "Expected no user to be found");
    }

    @Test
    @DisplayName("findByUsername should be case-sensitive")
    void testFindByUsername_caseSensitive() {
        Optional<AppUser> result = appUserRepository.findByUsername("JohnDoe");

        // Username stored as 'johndoe' — uppercase variant must not match
        assertFalse(result.isPresent(),
                "findByUsername should be case-sensitive and not match 'JohnDoe'");
    }

    // -----------------------------------------------------------------------
    // Standard CRUD via JpaRepository
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save should persist a new AppUser and assign an ID")
    void testSave_newUser() {
        AppUser newUser = new AppUser();
        newUser.setUsername("newuser");
        newUser.setPasswordHash("$2a$10$newhashedpassword");
        newUser.setRole("USER");
        newUser.setEmail("newuser@example.com");
        newUser.setCreatedAt(LocalDateTime.now());

        AppUser persisted = appUserRepository.save(newUser);

        assertNotNull(persisted.getId(), "Saved user should have an auto-generated ID");
        assertEquals("newuser", persisted.getUsername());
    }

    @Test
    @DisplayName("findById should return user when ID exists")
    void testFindById_found() {
        Optional<AppUser> result = appUserRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
    }

    @Test
    @DisplayName("findById should return empty Optional for a non-existent ID")
    void testFindById_notFound() {
        Optional<AppUser> result = appUserRepository.findById(9999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete should remove the user from the database")
    void testDelete() {
        appUserRepository.delete(savedUser);
        entityManager.flush();

        Optional<AppUser> result = appUserRepository.findById(savedUser.getId());
        assertFalse(result.isPresent(), "User should have been deleted");
    }

    @Test
    @DisplayName("findAll should return all persisted users")
    void testFindAll() {
        AppUser anotherUser = new AppUser();
        anotherUser.setUsername("seconduser");
        anotherUser.setPasswordHash("$2a$10$secondhash");
        anotherUser.setRole("ADMIN");
        anotherUser.setEmail("admin@example.com");
        anotherUser.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(anotherUser);

        long count = appUserRepository.count();

        assertTrue(count >= 2, "Should have at least 2 users in the database");
    }

    // -----------------------------------------------------------------------
    // Role-based retrieval
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Saved user should retain the ADMIN role after retrieval")
    void testAdminRolePersistence() {
        AppUser adminUser = new AppUser();
        adminUser.setUsername("adminuser");
        adminUser.setPasswordHash("$2a$10$adminhash");
        adminUser.setRole("ADMIN");
        adminUser.setEmail("admin@safeguard.co.za");
        adminUser.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(adminUser);

        Optional<AppUser> result = appUserRepository.findByUsername("adminuser");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRole());
    }

    @Test
    @DisplayName("Saved user should retain the USER role after retrieval")
    void testUserRolePersistence() {
        Optional<AppUser> result = appUserRepository.findByUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals("USER", result.get().getRole());
    }

    // -----------------------------------------------------------------------
    // Password hash integrity
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Password hash stored in the database should not be plain text")
    void testPasswordHashNotPlainText() {
        Optional<AppUser> result = appUserRepository.findByUsername("johndoe");

        assertTrue(result.isPresent());
        String hash = result.get().getPasswordHash();
        assertFalse(hash.isBlank(), "Hash must not be blank");
        // BCrypt hashes always start with $2a$, $2b$, or $2y$
        assertTrue(hash.startsWith("$2"), "Password must be BCrypt-hashed");
    }
}
