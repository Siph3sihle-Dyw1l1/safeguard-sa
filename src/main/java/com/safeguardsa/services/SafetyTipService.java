package com.safeguardsa.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SafetyTipService — Member C
 *
 * Handles the full tip submission lifecycle:
 *  1. Validates input — rejects descriptions containing phone numbers or
 *     email addresses to protect the submitter's anonymity.
 *  2. Resolves approximate latitude/longitude from province + city using
 *     a built-in SA coordinate lookup (no external API, no cost, no key).
 *  3. Saves the tip via SafetyTipEJB (Member D wires this in).
 *  4. Triggers area alert emails via NotificationService (Member D).
 */
@Service
public class SafetyTipService {

    // Patterns to screen descriptions for accidental personal info
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(\\+27|0)[6-8][0-9]{8}");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    // -----------------------------------------------------------------------
    // City coordinate lookup — avoids any external geocoding API
    // -----------------------------------------------------------------------
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("johannesburg",     new double[]{-26.2041, 28.0473});
        CITY_COORDS.put("pretoria",         new double[]{-25.7461, 28.1881});
        CITY_COORDS.put("tshwane",          new double[]{-25.7461, 28.1881});
        CITY_COORDS.put("soweto",           new double[]{-26.2677, 27.8588});
        CITY_COORDS.put("ekurhuleni",       new double[]{-26.3215, 28.1217});
        CITY_COORDS.put("cape town",        new double[]{-33.9249, 18.4241});
        CITY_COORDS.put("stellenbosch",     new double[]{-33.9321, 18.8602});
        CITY_COORDS.put("george",           new double[]{-33.9630, 22.4617});
        CITY_COORDS.put("durban",           new double[]{-29.8587, 31.0218});
        CITY_COORDS.put("pietermaritzburg", new double[]{-29.6006, 30.3794});
        CITY_COORDS.put("port elizabeth",   new double[]{-33.9608, 25.6022});
        CITY_COORDS.put("east london",      new double[]{-33.0153, 27.9116});
        CITY_COORDS.put("bloemfontein",     new double[]{-29.0852, 26.1596});
        CITY_COORDS.put("polokwane",        new double[]{-23.9045, 29.4689});
        CITY_COORDS.put("nelspruit",        new double[]{-25.4753, 30.9694});
        CITY_COORDS.put("mbombela",         new double[]{-25.4753, 30.9694});
        CITY_COORDS.put("rustenburg",       new double[]{-25.6672, 27.2423});
        CITY_COORDS.put("kimberley",        new double[]{-28.7323, 24.7620});
    }

    // Province centre fallbacks when city is not found in the lookup
    private static final Map<String, double[]> PROVINCE_CENTRES = new HashMap<>();
    static {
        PROVINCE_CENTRES.put("Gauteng",       new double[]{-26.2041, 28.0473});
        PROVINCE_CENTRES.put("Western Cape",  new double[]{-33.9249, 18.4241});
        PROVINCE_CENTRES.put("KwaZulu-Natal", new double[]{-29.8587, 31.0218});
        PROVINCE_CENTRES.put("Eastern Cape",  new double[]{-33.0153, 27.9116});
        PROVINCE_CENTRES.put("Free State",    new double[]{-29.0852, 26.1596});
        PROVINCE_CENTRES.put("Limpopo",       new double[]{-23.9045, 29.4689});
        PROVINCE_CENTRES.put("Mpumalanga",    new double[]{-25.4753, 30.9694});
        PROVINCE_CENTRES.put("North West",    new double[]{-25.6672, 27.2423});
        PROVINCE_CENTRES.put("Northern Cape", new double[]{-28.7323, 24.7620});
    }

    // -----------------------------------------------------------------------
    // Public methods
    // -----------------------------------------------------------------------

    /**
     * Validates and submits an anonymous safety tip.
     * Throws IllegalArgumentException if personal info is detected in the description.
     */
    public void submitTip(String province, String city, String streetArea,
                          String category, String description, String timeOfDay) {

        validateDescription(description);

        double[] coords = resolveCoordinates(province, city);

        // TODO: inject SafetyTipEJB (Member D) and replace this line:
        // safetyTipEJB.save(province, city, streetArea, category,
        //                   description, timeOfDay, coords[0], coords[1]);

        // TODO: inject NotificationService (Member D) and replace this line:
        // notificationService.notifyAreaSubscribers(province, city);

        System.out.printf(
            "[SafetyTipService] Tip queued — %s / %s / %s @ [%.4f, %.4f]%n",
            province, city, category, coords[0], coords[1]);
    }

    /** Returns the list of SA provinces for the tip form dropdown. */
    public List<String> getAllProvinces() {
        return Arrays.asList(
            "Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape",
            "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape"
        );
    }

    /** Returns tip categories for the tip form dropdown. */
    public List<String> getAllCategories() {
        return Arrays.asList("CRIME", "ASSAULT", "THEFT", "SUSPICIOUS", "OTHER");
    }

    /** Returns time-of-day options for the tip form dropdown. */
    public List<String> getTimesOfDay() {
        return Arrays.asList("Morning", "Afternoon", "Evening", "Night");
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /** Rejects descriptions that contain phone numbers or email addresses. */
    private void validateDescription(String description) {
        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters.");
        }
        if (PHONE_PATTERN.matcher(description).find()) {
            throw new IllegalArgumentException(
                "Please remove any phone numbers from your description to protect your anonymity.");
        }
        if (EMAIL_PATTERN.matcher(description).find()) {
            throw new IllegalArgumentException(
                "Please remove any email addresses from your description to protect your anonymity.");
        }
    }

    /**
     * Looks up coordinates for the given city (case-insensitive).
     * Falls back to the province centre if the city is not in the table.
     * Falls back to the centre of South Africa if neither is found.
     */
    private double[] resolveCoordinates(String province, String city) {
        if (city != null) {
            double[] coords = CITY_COORDS.get(city.trim().toLowerCase());
            if (coords != null) return coords;
        }
        double[] fallback = PROVINCE_CENTRES.get(province);
        return (fallback != null) ? fallback : new double[]{-28.4793, 24.6727};
    }
}
