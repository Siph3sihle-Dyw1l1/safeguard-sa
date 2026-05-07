package com.safeguardsa.services;

import com.safeguardsa.models.AppUser;
import com.safeguardsa.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for AppUser management.
 * Implements UserDetailsService so Spring Security can use it for login.
 *
 * @author SafeGuardSA
 */
@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }


    public AppUser registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken: " + username);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered: " + email);
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        // role defaults to "USER" via @PrePersist — no need to set it here

        return userRepository.save(user);
    }


    public AppUser registerAdmin(String username, String password, String email) {
        AppUser admin = registerUser(username, password, email);
        admin.setRole("ADMIN");
        return userRepository.save(admin);
    }


    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }


    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }


    public List<AppUser> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }


    public AppUser changePassword(Long userId, String newPassword) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }


    public AppUser updateRole(Long userId, String newRole) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setRole(newRole);
        return userRepository.save(user);
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Cannot delete — user not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }


    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }


    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
