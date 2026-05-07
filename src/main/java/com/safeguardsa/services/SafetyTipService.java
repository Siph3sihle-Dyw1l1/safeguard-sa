package com.safeguardsa.services;

import com.safeguardsa.models.SafetyTip;
import com.safeguardsa.repositories.SafetyTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SafetyTipService {

    @Autowired
    private SafetyTipRepository safetyTipRepository; // Added repository injection 

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+27|0)[6-8][0-9]{8}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("johannesburg", new double[]{-26.2041, 28.0473});
        CITY_COORDS.put("pretoria", new double[]{-25.7461, 28.1881});
        CITY_COORDS.put("cape town", new double[]{-33.9249, 18.4241});
        CITY_COORDS.put("durban", new double[]{-29.8587, 31.0218});
        CITY_COORDS.put("polokwane", new double[]{-23.9045, 29.4689});
    }

    public void submitTip(String province, String city, String streetArea, String category, String description, String timeOfDay) {
    validateDescription(description);
    
    // This returns an array: e.g., [-26.2041, 28.0473]
    double[] coords = resolveCoordinates(province, city);

    SafetyTip tip = new SafetyTip();
    tip.setProvince(province);
    tip.setCity(city);
    tip.setStreetArea(streetArea);
    tip.setCategory(category);
    tip.setDescription(description);
    tip.setTimeOfDay(timeOfDay);
    
    // ✅ FIXED: Add to extract the Latitude value
    tip.setLatitude(coords[0]);  
    
    // ✅ FIXED: Ensure this also stays as [1] for Longitude
    tip.setLongitude(coords[1]); 
    
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

    private double[] resolveCoordinates(String province, String city) {
        if (city != null && CITY_COORDS.containsKey(city.trim().toLowerCase())) {
            return CITY_COORDS.get(city.trim().toLowerCase());
        }
        return new double[]{-28.4793, 24.6727}; // SA Center fallback
    }

    public List<String> getAllProvinces() { return Arrays.asList("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape"); }
    public List<String> getAllCategories() { return Arrays.asList("CRIME", "ASSAULT", "THEFT", "SUSPICIOUS", "OTHER"); }
    public List<String> getTimesOfDay() { return Arrays.asList("Morning", "Afternoon", "Evening", "Night"); }
}