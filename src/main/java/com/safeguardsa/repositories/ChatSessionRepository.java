/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.safeguardsa.repositories;

import com.safeguardsa.models.AppUser;
import com.safeguardsa.models.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author ntsak
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    // --- Find anonymous session by token ---
    Optional<ChatSession> findBySessionToken(String sessionToken);

    // --- Find all sessions for a logged-in user (chat history) ---
    List<ChatSession> findByUserOrderByLastActiveAtDesc(AppUser user);

    // --- Check if a session token already exists ---
    boolean existsBySessionToken(String sessionToken);
}
