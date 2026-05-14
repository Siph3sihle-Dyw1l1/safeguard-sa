package com.safeguardsa.services;

import com.safeguardsa.ejbs.StatsEJB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private StatsEJB statsEJB;

    // ---------------------------------------------------------------
    // STAT CARDS
    // ---------------------------------------------------------------
    public Map<String, Long> getStatCards() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("totalTips", statsEJB.getTotalTips());
        stats.put("pendingTips", statsEJB.getPendingTips());
        stats.put("approvedTips", statsEJB.getApprovedTips());
        stats.put("flaggedTips", statsEJB.getFlaggedTips());
        stats.put("chatQueries", statsEJB.getTotalChatQueries());
        stats.put("emergencyTriggers", statsEJB.getEmergencyTriggers());
        return stats;
    }

    // ---------------------------------------------------------------
    // CHART: Tips by Category — Doughnut chart
    // ---------------------------------------------------------------
    public Map<String, Object> getTipsByCategoryChart() {
        List<Object[]> rows = statsEJB.getTipsByCategory();
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            data.add((Long) row[1]);
        }

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("labels", labels);
        chart.put("data", data);
        chart.put("backgroundColors", List.of(
                "#e74c3c", // CRIME — red
                "#8e44ad", // ASSAULT — darkred/purple
                "#e67e22", // THEFT — orange
                "#f1c40f", // SUSPICIOUS — gold
                "#95a5a6" // OTHER — grey
        ));
        return chart;
    }

    // ---------------------------------------------------------------
    // CHART: Tips by Province — Bar chart
    // ---------------------------------------------------------------
    public Map<String, Object> getTipsByProvinceChart() {
        List<Object[]> rows = statsEJB.getTipsByProvince();
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            data.add((Long) row[1]);
        }

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("labels", labels);
        chart.put("data", data);
        chart.put("backgroundColor", "#3498db");
        return chart;
    }

    // ---------------------------------------------------------------
    // CHART: Monthly trend — Line chart (Tips + Chat queries)
    // ---------------------------------------------------------------
    public Map<String, Object> getMonthlyTrendChart() {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        // Build zero-filled arrays for all 12 months
        long[] tipCounts = new long[12];
        long[] chatCounts = new long[12];

        for (Object[] row : statsEJB.getTipsByMonth()) {
            int month = ((Number) row[0]).intValue(); // 1-based
            tipCounts[month - 1] = (Long) row[1];
        }

        for (Object[] row : statsEJB.getChatByMonth()) {
            int month = ((Number) row[0]).intValue();
            chatCounts[month - 1] = (Long) row[1];
        }

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("labels", Arrays.asList(monthNames));
        chart.put("tipData", toList(tipCounts));
        chart.put("chatData", toList(chatCounts));
        return chart;
    }

    private List<Long> toList(long[] arr) {
        List<Long> list = new ArrayList<>();
        for (long v : arr) {
            list.add(v);
        }
        return list;
    }
}
