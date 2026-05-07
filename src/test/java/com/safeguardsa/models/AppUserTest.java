package com.safeguardsa.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AppUser entity (Member E — Security & QA).
 * Tests field defaults, role assignment, and basic validation behaviour.
 */
@DisplayName("AppUser Model Tests")
class AppUserTest {

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setRole("USER");
        user.setEmail("testuser@example.com");
        user.setCreatedAt(LocalDateTime.now());
    }

    // -----------------------------------------------------------------------
    // Field getters / setters
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Should correctly store and retrieve username")
    void testUsernameGetterSetter() {
        user.setUsername("alice");
        assertEquals("alice", user.getUsername());
    }

    @Test
    @DisplayName("Should correctly store and retrieve password hash")
    void testPasswordHashGetterSetter() {
        String hash = "$2a$10$examplehash";
        user.setPasswordHash(hash);
        assertEquals(hash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Should correctly store and retrieve email")
    void testEmailGetterSetter() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should correctly store and retrieve role USER")
    void testRoleUser() {
        user.setRole("USER");
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("Should correctly store and retrieve role ADMIN")
    void testRoleAdmin() {
        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    @DisplayName("Should correctly store and retrieve ID")
    void testIdGetterSetter() {
        user.setId(99L);
        assertEquals(99L, user.getId());
    }

    @Test
    @DisplayName("Should correctly store and retrieve createdAt timestamp")
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    // -----------------------------------------------------------------------
    // Null / boundary checks
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Username should not be null after set")
    void testUsernameNotNull() {
        assertNotNull(user.getUsername());
    }

    @Test
    @DisplayName("Password hash should not be null after set")
    void testPasswordHashNotNull() {
        assertNotNull(user.getPasswordHash());
    }

    @Test
    @DisplayName("Role should not be null after set")
    void testRoleNotNull() {
        assertNotNull(user.getRole());
    }

    @Test
    @DisplayName("Email should not be null after set")
    void testEmailNotNull() {
        assertNotNull(user.getEmail());
    }

    // -----------------------------------------------------------------------
    // Equality / identity
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Two AppUser objects with same username should have equal usernames")
    void testUsernameEquality() {
        AppUser anotherUser = new AppUser();
        anotherUser.setUsername("testuser");
        assertEquals(user.getUsername(), anotherUser.getUsername());
    }

    @Test
    @DisplayName("Two AppUser objects with different IDs should not be equal by ID")
    void testDifferentIds() {
        AppUser anotherUser = new AppUser();
        anotherUser.setId(2L);
        assertNotEquals(user.getId(), anotherUser.getId());
    }

    // -----------------------------------------------------------------------
    // Role logic guards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Role should be USER or ADMIN only")
    void testRoleIsValidValue() {
        String role = user.getRole();
        assertTrue(role.equals("USER") || role.equals("ADMIN"),
                "Role must be USER or ADMIN, got: " + role);
    }
}
