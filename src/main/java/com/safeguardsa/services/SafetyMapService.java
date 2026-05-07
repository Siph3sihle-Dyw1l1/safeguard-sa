package com.safeguardsa.services;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * SafetyMapService — Member C
 *
 * Fetches all APPROVED safety tips and prepares them for Leaflet.js. Returns
 * structured data (province, category, coordinates, metadata) consumed by
 * SafetyMapController → map.js.
 *
 * NOTE: SafetyTipRepository is owned by Member D. The method below uses sample
 * data so Member C can build and test the Leaflet.js map independently. Once
 * Member D's repository is ready, replace the sample data block with:
 *
 * return safetyTipRepository.findByStatus("APPROVED")
 * .stream().map(this::toMapDto).collect(Collectors.toList());
 */
@Service
public class SafetyMapService {

    /**
     * Returns all approved tips as a list of Maps.Each map is one JSON object
     * sent to Leaflet.js in map.js. Keys per tip: id, province, city,
     * streetArea, category, timeOfDay, description, submittedAt, latitude,
     * longitude
     *
     * @return
     */
    public List<Map<String, Object>> getApprovedTipsForMap() {

        // -------------------------------------------------------------------
        // TODO: replace this sample data with the real repository call
        // once SafetyTipRepository (Member D) is available:
        //
        // return safetyTipRepository.findByStatus("APPROVED")
        //         .stream().map(this::toMapDto).collect(Collectors.toList());
        // -------------------------------------------------------------------
        List<Map<String, Object>> tips = new ArrayList<>();

        tips.add(tip(1L, "Gauteng", "Johannesburg",
                "Noord St taxi rank", "THEFT", "Evening",
                "Pickpocketing reported near the main entrance.",
                "2026-04-28", -26.2023, 28.0436));

        tips.add(tip(2L, "Gauteng", "Pretoria",
                "Sunnyside — Block F alley", "SUSPICIOUS", "Night",
                "Group loitering behind the building after midnight.",
                "2026-04-29", -25.7534, 28.2170));

        tips.add(tip(3L, "Western Cape", "Cape Town",
                "Long Street near Loader St", "ASSAULT", "Night",
                "Mugging reported near the corner after midnight.",
                "2026-04-30", -33.9226, 18.4190));

        tips.add(tip(4L, "KwaZulu-Natal", "Durban",
                "Warwick Junction pedestrian bridge", "CRIME", "Afternoon",
                "Phone snatching reported on the elevated walkway.",
                "2026-05-01", -29.8578, 31.0107));

        tips.add(tip(5L, "Eastern Cape", "Port Elizabeth",
                "Central — Govan Mbeki Ave", "THEFT", "Morning",
                "Car break-ins reported in the open parking area.",
                "2026-05-02", -33.9585, 25.5973));

        return tips;
    }

    /**
     * Returns province list for the map filter panel.
     *
     * @return
     */
    public List<String> getAllProvinces() {
        return Arrays.asList(
                "All Provinces", "Gauteng", "Western Cape", "KwaZulu-Natal",
                "Eastern Cape", "Free State", "Limpopo", "Mpumalanga",
                "North West", "Northern Cape"
        );
    }

    /**
     * Returns category list for the map filter panel.
     *
     * @return
     */
    public List<String> getAllCategories() {
        return Arrays.asList(
                "All Categories", "CRIME", "ASSAULT", "THEFT", "SUSPICIOUS", "OTHER"
        );
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------
    private Map<String, Object> tip(Long id, String province, String city,
            String streetArea, String category,
            String timeOfDay, String description,
            String submittedAt, double lat, double lng) {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("id", id);
        t.put("province", province);
        t.put("city", city);
        t.put("streetArea", streetArea);
        t.put("category", category);
        t.put("timeOfDay", timeOfDay);
        t.put("description", description);
        t.put("submittedAt", submittedAt);
        t.put("latitude", lat);
        t.put("longitude", lng);
        return t;
    }

    /**
     * Converts a SafetyTip entity to the DTO consumed by Leaflet.js. Uncomment
     * once SafetyTip entity (Member D) is available.
     */
    /*
    private Map<String, Object> toMapDto(SafetyTip t) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",          t.getId());
        dto.put("province",    t.getProvince());
        dto.put("city",        t.getCity());
        dto.put("streetArea",  t.getStreetArea());
        dto.put("category",    t.getCategory());
        dto.put("timeOfDay",   t.getTimeOfDay());
        dto.put("description", t.getDescription());
        dto.put("submittedAt", t.getSubmittedAt().toString());
        dto.put("latitude",    t.getLatitude());
        dto.put("longitude",   t.getLongitude());
        return dto;
    }
     */
}
