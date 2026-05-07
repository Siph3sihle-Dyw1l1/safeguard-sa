package com.safeguardsa.services;

import com.safeguardsa.models.SafetyTip;
import com.safeguardsa.repositories.SafetyTipRepository; // IMPORT THIS
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyMapService {

    @Autowired
    private SafetyTipRepository safetyTipRepository; // DECLARE THIS

    public List<Map<String, Object>> getApprovedTipsForMap() {
        // Fetch only approved tips with coordinates from the database
        return safetyTipRepository.findApprovedWithCoordinates()
                .stream()
                .map(this::toMapDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert the Database Entity to a Map for JavaScript
    private Map<String, Object> toMapDto(SafetyTip t) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", t.getId());
        dto.put("province", t.getProvince());
        dto.put("city", t.getCity());
        dto.put("streetArea", t.getStreetArea());
        dto.put("category", t.getCategory());
        dto.put("timeOfDay", t.getTimeOfDay());
        dto.put("description", t.getDescription());
        dto.put("submittedAt", t.getSubmittedAt().toString());
        dto.put("latitude", t.getLatitude());
        dto.put("longitude", t.getLongitude());
        return dto;
    }

    public List<String> getAllProvinces() {
        return Arrays.asList("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape");
    }

    public List<String> getAllCategories() {
        return Arrays.asList("CRIME", "ASSAULT", "THEFT", "SUSPICIOUS", "OTHER");
    }
}