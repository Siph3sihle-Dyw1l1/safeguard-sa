package com.safeguardsa.services;

import com.safeguardsa.models.SafetyTip;
import com.safeguardsa.repositories.SafetyTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author sphes
 */
@Service
public class SafetyTipService {

    @Autowired
    private SafetyTipRepository safetyTipRepository; // Added repository injection 

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+27|0)[6-8][0-9]{8}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    // ✅ NOTICE: The hardcoded CITY_COORDS map has been completely removed!
    public void submitTip(String province, String city, String streetArea, String category, String description, String timeOfDay, Double latitude, Double longitude) {
        validateDescription(description);

        SafetyTip tip = new SafetyTip();
        tip.setProvince(province);
        tip.setCity(city);
        tip.setStreetArea(streetArea);
        tip.setCategory(category);
        tip.setDescription(description);
        tip.setTimeOfDay(timeOfDay);

        // ✅ FIXED: Use the exact coordinates passed from the frontend map search
        if (latitude != null && longitude != null) {
            tip.setLatitude(latitude);
            tip.setLongitude(longitude);
        } else {
            // Fallback just in case the search fails or the user submits without clicking a suggestion
            tip.setLatitude(-28.4793);
            tip.setLongitude(24.6727);
        }

        tip.setStatus("PENDING");

        safetyTipRepository.save(tip);
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters.");
        }
        if (PHONE_PATTERN.matcher(description).find() || EMAIL_PATTERN.matcher(description).find()) {
            throw new IllegalArgumentException("Personal contact info detected. Please keep tips anonymous.");
        }
    }

    // ✅ NOTICE: The resolveCoordinates() method was deleted because we don't need to guess anymore!
    public List<String> getAllProvinces() {
        return Arrays.asList("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape");
    }

    public List<String> getAllCategories() {
        return Arrays.asList("CRIME", "ASSAULT", "THEFT", "SUSPICIOUS", "OTHER");
    }

    public List<String> getTimesOfDay() {
        return Arrays.asList("Morning", "Afternoon", "Evening", "Night");
    }
}
