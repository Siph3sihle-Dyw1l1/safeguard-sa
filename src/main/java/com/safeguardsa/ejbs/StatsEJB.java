/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safeguardsa.ejbs;

import com.safeguardsa.repositories.ChatMessageRepository;
import com.safeguardsa.repositories.SafetyTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author ntsak
 * StatsEJB — aggregates statistics across both modules for the admin dashboard.
 * Results are cached for 60 seconds to prevent heavy repeated queries.
 */
@Component
public class StatsEJB {

    @Autowired
    private SafetyTipRepository safetyTipRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // Simple in-memory cache
    private Map<String, Object> cache = new HashMap<>();
    private LocalDateTime cacheExpiry = LocalDateTime.MIN;
    private static final int CACHE_SECONDS = 60;

    private void refreshIfExpired() {
        if (LocalDateTime.now().isAfter(cacheExpiry)) {
            cache.clear();

            // Safety tip counts
            cache.put("totalTips", safetyTipRepository.count());
            cache.put("pendingTips", safetyTipRepository.countByStatus("PENDING"));
            cache.put("approvedTips", safetyTipRepository.countByStatus("APPROVED"));
            cache.put("flaggedTips", safetyTipRepository.countByStatus("FLAGGED"));

            // Chatbot counts
            cache.put("totalChatQueries", chatMessageRepository.countUserMessages());
            cache.put("emergencyTriggers", chatMessageRepository.countEmergencyMessages());

            // Grouped stats for charts
            cache.put("tipsByCategory", safetyTipRepository.countByCategoryGrouped());
            cache.put("tipsByProvince", safetyTipRepository.countByProvinceGrouped());

            LocalDateTime oneYearAgo = LocalDateTime.now().minusMonths(12);
            cache.put("tipsByMonth", safetyTipRepository.countByMonthSince(oneYearAgo));
            cache.put("chatByMonth", chatMessageRepository.countByMonthSince(oneYearAgo));

            cacheExpiry = LocalDateTime.now().plusSeconds(CACHE_SECONDS);
        }
    }

    public long getTotalTips() {
        refreshIfExpired();
        return (long) cache.get("totalTips");
    }

    public long getPendingTips() {
        refreshIfExpired();
        return (long) cache.get("pendingTips");
    }

    public long getApprovedTips() {
        refreshIfExpired();
        return (long) cache.get("approvedTips");
    }

    public long getFlaggedTips() {
        refreshIfExpired();
        return (long) cache.get("flaggedTips");
    }

    public long getTotalChatQueries() {
        refreshIfExpired();
        return (long) cache.get("totalChatQueries");
    }

    public long getEmergencyTriggers() {
        refreshIfExpired();
        return (long) cache.get("emergencyTriggers");
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getTipsByCategory() {
        refreshIfExpired();
        return (List<Object[]>) cache.get("tipsByCategory");
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getTipsByProvince() {
        refreshIfExpired();
        return (List<Object[]>) cache.get("tipsByProvince");
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getTipsByMonth() {
        refreshIfExpired();
        return (List<Object[]>) cache.get("tipsByMonth");
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getChatByMonth() {
        refreshIfExpired();
        return (List<Object[]>) cache.get("chatByMonth");
    }

    /** Force a cache refresh — call after approving/flagging a tip */
    public void invalidateCache() {
        cacheExpiry = LocalDateTime.MIN;
    }
}