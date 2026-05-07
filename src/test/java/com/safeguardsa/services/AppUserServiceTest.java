package com.safeguardsa.services;

import com.safeguardsa.models.AppUser;
import com.safeguardsa.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Service-layer tests for AppUserService (Member E — Security & QA).
 *
 * Uses Mockito to isolate the service from the real database.
 * registerUser(username, password, email) — 3 params, role defaults to USER via @PrePersist.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserService Tests")
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser existingUser;

    // -----------------------------------------------------------------------
    // Test data
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setUsername("johndoe");
        existingUser.setPasswordHash("$2a$10$hashedpassword");
        existingUser.setRole("USER");
        existingUser.setEmail("johndoe@example.com");
        existingUser.setCreatedAt(LocalDateTime.now());
    }

    // -----------------------------------------------------------------------
    // loadUserByUsername (Spring Security UserDetailsService)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when username exists")
    void testLoadUserByUsername_found() {
        when(appUserRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(existingUser));

        UserDetails details = appUserService.loadUserByUsername("johndoe");

        assertNotNull(details);
        assertEquals("johndoe", details.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsername_notFound() {
        when(appUserRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> appUserService.loadUserByUsername("ghost"),
                "Should throw UsernameNotFoundException for unknown username");
    }

    @Test
    @DisplayName("loadUserByUsername should map the stored BCrypt hash to UserDetails password")
    void testLoadUserByUsername_passwordIsMapped() {
        when(appUserRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(existingUser));

        UserDetails details = appUserService.loadUserByUsername("johndoe");

        assertEquals("$2a$10$hashedpassword", details.getPassword(),
                "UserDetails password should equal the stored BCrypt hash");
    }

    @Test
    @DisplayName("loadUserByUsername should assign ROLE_USER authority for a USER account")
    void testLoadUserByUsername_userRole() {
        when(appUserRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(existingUser));

        UserDetails details = appUserService.loadUserByUsername("johndoe");

        boolean hasUserRole = details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        assertTrue(hasUserRole, "USER account should have ROLE_USER authority");
    }

    @Test
    @DisplayName("loadUserByUsername should assign ROLE_ADMIN authority for an ADMIN account")
    void testLoadUserByUsername_adminRole() {
        existingUser.setRole("ADMIN");
        when(appUserRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(existingUser));

        UserDetails details = appUserService.loadUserByUsername("johndoe");

        boolean hasAdminRole = details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        assertTrue(hasAdminRole, "ADMIN account should have ROLE_ADMIN authority");
    }

    // -----------------------------------------------------------------------
    // registerUser(username, password, email) — 3 params
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("registerUser should encode the raw password with BCrypt before saving")
    void testRegisterUser_passwordIsHashed() {
        when(appUserRepository.existsByUsername("newuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawpassword123")).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registered = appUserService.registerUser("newuser", "rawpassword123",
                "newuser@example.com");

        assertEquals("$2a$10$mockedhash", registered.getPasswordHash(),
                "Plain-text password must never be stored");
        verify(passwordEncoder, times(1)).encode("rawpassword123");
    }

    @Test
    @DisplayName("registerUser should NOT store the raw plain-text password")
    void testRegisterUser_rawPasswordNotStored() {
        when(appUserRepository.existsByUsername("newuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registered = appUserService.registerUser("newuser", "rawpassword123",
                "newuser@example.com");

        assertNotEquals("rawpassword123", registered.getPasswordHash(),
                "Raw password must never be stored in the database");
    }

    @Test
    @DisplayName("registerUser should call repository save exactly once")
    void testRegisterUser_saveCalledOnce() {
        when(appUserRepository.existsByUsername("user2")).thenReturn(false);
        when(appUserRepository.existsByEmail("user2@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        appUserService.registerUser("user2", "pass2", "user2@example.com");

        verify(appUserRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    @DisplayName("registerUser should persist the correct username")
    void testRegisterUser_usernameIsPersisted() {
        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registered = appUserService.registerUser("alice", "alicepass",
                "alice@example.com");

        assertEquals("alice", registered.getUsername());
    }

    @Test
    @DisplayName("registerUser should persist the correct email")
    void testRegisterUser_emailIsPersisted() {
        when(appUserRepository.existsByUsername("alice")).thenReturn(false);
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registered = appUserService.registerUser("alice", "alicepass",
                "alice@example.com");

        assertEquals("alice@example.com", registered.getEmail());
    }

    // -----------------------------------------------------------------------
    // findByUsername helper method
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findByUsername should return Optional with user when found")
    void testFindByUsername_found() {
        when(appUserRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(existingUser));

        Optional<AppUser> result = appUserService.findByUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
    }

    @Test
    @DisplayName("findByUsername should return empty Optional when user is not found")
    void testFindByUsername_notFound() {
        when(appUserRepository.findByUsername("nobody"))
                .thenReturn(Optional.empty());

        Optional<AppUser> result = appUserService.findByUsername("nobody");

        assertFalse(result.isPresent());
    }

    // -----------------------------------------------------------------------
    // Duplicate username / email guards
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("registerUser should throw IllegalArgumentException if username is already taken")
    void testRegisterUser_duplicateUsername() {
        when(appUserRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> appUserService.registerUser("johndoe", "pass", "other@example.com"),
                "Should reject registration with a duplicate username");
    }

    @Test
    @DisplayName("registerUser should throw IllegalArgumentException if email is already registered")
    void testRegisterUser_duplicateEmail() {
        when(appUserRepository.existsByUsername("brandnew")).thenReturn(false);
        when(appUserRepository.existsByEmail("johndoe@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> appUserService.registerUser("brandnew", "pass", "johndoe@example.com"),
                "Should reject registration with a duplicate email");
    }

    // -----------------------------------------------------------------------
    // registerAdmin
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("registerAdmin should save twice — once for user, once to set ADMIN role")
    void testRegisterAdmin_saveCalledTwice() {
        when(appUserRepository.existsByUsername("adminuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("admin@safeguard.co.za")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        appUserService.registerAdmin("adminuser", "adminpass", "admin@safeguard.co.za");

        verify(appUserRepository, times(2)).save(any(AppUser.class));
    }

    @Test
    @DisplayName("registerAdmin should set role to ADMIN")
    void testRegisterAdmin_roleIsAdmin() {
        when(appUserRepository.existsByUsername("adminuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("admin@safeguard.co.za")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$mockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser admin = appUserService.registerAdmin("adminuser", "adminpass",
                "admin@safeguard.co.za");

        assertEquals("ADMIN", admin.getRole());
    }

    // -----------------------------------------------------------------------
    // changePassword
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("changePassword should encode the new password before saving")
    void testChangePassword_encodesNewPassword() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("$2a$10$newmockedhash");
        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser updated = appUserService.changePassword(1L, "newpass123");

        assertEquals("$2a$10$newmockedhash", updated.getPasswordHash());
    }

    @Test
    @DisplayName("changePassword should throw IllegalArgumentException for unknown user ID")
    void testChangePassword_userNotFound() {
        when(appUserRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> appUserService.changePassword(999L, "newpass"),
                "Should throw when user ID does not exist");
    }

    // -----------------------------------------------------------------------
    // deleteUser
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteUser should call deleteById when user exists")
    void testDeleteUser_success() {
        when(appUserRepository.existsById(1L)).thenReturn(true);

        appUserService.deleteUser(1L);

        verify(appUserRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser should throw IllegalArgumentException when user does not exist")
    void testDeleteUser_notFound() {
        when(appUserRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> appUserService.deleteUser(999L),
                "Should throw when trying to delete a non-existent user");
    }

    // -----------------------------------------------------------------------
    // isUsernameTaken / isEmailTaken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("isUsernameTaken should return true when username exists")
    void testIsUsernameTaken_true() {
        when(appUserRepository.existsByUsername("johndoe")).thenReturn(true);
        assertTrue(appUserService.isUsernameTaken("johndoe"));
    }

    @Test
    @DisplayName("isUsernameTaken should return false when username does not exist")
    void testIsUsernameTaken_false() {
        when(appUserRepository.existsByUsername("newguy")).thenReturn(false);
        assertFalse(appUserService.isUsernameTaken("newguy"));
    }

    @Test
    @DisplayName("isEmailTaken should return true when email exists")
    void testIsEmailTaken_true() {
        when(appUserRepository.existsByEmail("johndoe@example.com")).thenReturn(true);
        assertTrue(appUserService.isEmailTaken("johndoe@example.com"));
    }

    @Test
    @DisplayName("isEmailTaken should return false when email does not exist")
    void testIsEmailTaken_false() {
        when(appUserRepository.existsByEmail("fresh@example.com")).thenReturn(false);
        assertFalse(appUserService.isEmailTaken("fresh@example.com"));
    }
}