/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.safeguardsa.repositories;

import com.safeguardsa.models.ChatMessage;
import com.safeguardsa.models.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author ntsak
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // --- Fetch all messages in a session (for chat history display) ---
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);

    // --- Admin: fetch all Safety Gate-triggered messages ---
    @Query("SELECT m FROM ChatMessage m WHERE m.isEmergency = true ORDER BY m.createdAt DESC")
    List<ChatMessage> findEmergencyMessages();

    // --- Dashboard: count all Safety Gate triggers ---
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.isEmergency = true")
    long countEmergencyMessages();

    // --- Dashboard: count total chatbot queries (USER role messages) ---
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.role = 'USER'")
    long countUserMessages();

    // --- Dashboard: monthly chatbot query trend ---
    @Query("SELECT MONTH(m.createdAt), COUNT(m) FROM ChatMessage m WHERE m.role = 'USER' AND m.createdAt >= :from GROUP BY MONTH(m.createdAt) ORDER BY MONTH(m.createdAt)")
    List<Object[]> countByMonthSince(@Param("from") LocalDateTime from);
}
