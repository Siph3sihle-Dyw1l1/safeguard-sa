package com.safeguardsa.services;
 
import org.springframework.stereotype.Service;
 
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
 
/**
 * SafetyGateService - Emergency keyword scanner.
 *
 * Scans EVERY incoming chat message BEFORE calling Gemini.
 * If an emergency keyword is detected, Gemini is NEVER called.
 * Returns a SafetyCheckResult with category, keyword, and emergency contacts.
 *
 * @author mmaphutijkgomo
 */
@Service
public class SafetyGateService {
 
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();
 
    static {
        CATEGORY_KEYWORDS.put("MENTAL_HEALTH_CRISIS", Arrays.asList(
                "suicide", "kill myself", "end my life", "want to die",
                "hanging", "self harm", "self-harm", "cut myself",
                "overdose", "drug overdose", "swallowed pills", "not worth living"
        ));
 
        CATEGORY_KEYWORDS.put("PHYSICAL_EMERGENCY", Arrays.asList(
                "chest pain", "heart attack", "not breathing", "cannot breathe",
                "can't breathe", "stop breathing", "no pulse", "unconscious",
                "unresponsive", "fainted", "passed out", "seizure", "stroke",
                "anaphylaxis", "allergic reaction", "poisoning", "swallowed poison",
                "bleeding heavily", "won't stop bleeding"
        ));
 
        CATEGORY_KEYWORDS.put("ASSAULT_VIOLENCE", Arrays.asList(
                "stabbed", "shot", "attacked", "being attacked",
                "rape", "sexual assault", "someone is hurting me",
                "being abused", "domestic violence"
        ));
 
        CATEGORY_KEYWORDS.put("IMMEDIATE_DANGER", Arrays.asList(
                "dying", "going to die", "choking", "drowning",
                "bleeding", "someone collapsed"
        ));
    }
 
    /**
     * Check a message against all emergency keyword categories.
     *
     * @param message the user's chat message
     * @return SafetyCheckResult — always non-null
     */
    public SafetyCheckResult checkMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return SafetyCheckResult.safe();
        }
 
        String lower = message.toLowerCase();
 
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            for (String keyword : entry.getValue()) {
                if (lower.contains(keyword)) {
                    EmergencyResponse emergencyResponse = buildEmergencyResponse(category);
                    return SafetyCheckResult.emergency(category, keyword, emergencyResponse);
                }
            }
        }
 
        return SafetyCheckResult.safe();
    }
 
    /**
     * Convenience method — returns true if the message is an emergency.
     */
    public boolean isEmergency(String message) {
        return checkMessage(message).isEmergency();
    }
 
    private EmergencyResponse buildEmergencyResponse(String category) {
        switch (category) {
            case "MENTAL_HEALTH_CRISIS":
                return new EmergencyResponse(
                        "Mental Health Crisis Support",
                        "You are not alone, and help is available right now.",
                        Arrays.asList(
                                new EmergencyContact("SADAG 24hr Helpline",  "0800 567 567", "Free, confidential 24/7 counselling"),
                                new EmergencyContact("Suicide Crisis Line",   "0800 567 567", "Immediate support for suicidal thoughts"),
                                new EmergencyContact("LifeLine SA",          "0861 322 322", "24/7 crisis intervention"),
                                new EmergencyContact("Police Emergency",     "10111",        "SAPS — if you are in immediate danger"),
                                new EmergencyContact("All Emergencies",      "112",          "Cell phone emergency line (free)")
                        ),
                        true
                );
 
            case "PHYSICAL_EMERGENCY":
                return new EmergencyResponse(
                        "Medical Emergency",
                        "This sounds like a medical emergency. Please call emergency services right away.",
                        Arrays.asList(
                                new EmergencyContact("National Ambulance", "10177",        "Government ambulance service"),
                                new EmergencyContact("All Emergencies",   "112",          "Free from any cell phone"),
                                new EmergencyContact("Police Emergency",  "10111",        "SAPS emergency line"),
                                new EmergencyContact("Netcare 911",       "082 911",      "Private ambulance"),
                                new EmergencyContact("Poison Helpline",   "0861 555 777", "For poisoning emergencies")
                        ),
                        false
                );
 
            case "ASSAULT_VIOLENCE":
                return new EmergencyResponse(
                        "Violence and Assault Support",
                        "Your safety is the most important thing right now.",
                        Arrays.asList(
                                new EmergencyContact("Police Emergency",               "10111",        "SAPS — for immediate help"),
                                new EmergencyContact("All Emergencies",               "112",          "Cell phone emergency (free)"),
                                new EmergencyContact("Stop Gender Violence Helpline", "0800 150 150", "Free, 24/7 support"),
                                new EmergencyContact("Childline SA",                  "0800 055 555", "For children and young people"),
                                new EmergencyContact("Lifeline GBV Support",          "0800 428 428", "Gender-based violence support")
                        ),
                        false
                );
 
            case "IMMEDIATE_DANGER":
            default:
                return new EmergencyResponse(
                        "Emergency Assistance Required",
                        "This appears to be an emergency. Please contact emergency services immediately.",
                        Arrays.asList(
                                new EmergencyContact("Police Emergency", "10111",   "SAPS emergency response"),
                                new EmergencyContact("All Emergencies", "112",     "Universal emergency (free from cell)"),
                                new EmergencyContact("Ambulance",       "10177",   "Medical emergency"),
                                new EmergencyContact("Netcare 911",     "082 911", "Private ambulance"),
                                new EmergencyContact("ER24",            "084 124", "Private emergency")
                        ),
                        false
                );
        }
    }
 
    // ── Inner classes ────────────────────────────────────────────────────
 
    public static class SafetyCheckResult {
        private final boolean emergency;
        private final String category;
        private final String triggeredKeyword;
        private final EmergencyResponse emergencyResponse;
 
        private SafetyCheckResult(boolean emergency, String category,
                                   String triggeredKeyword, EmergencyResponse emergencyResponse) {
            this.emergency         = emergency;
            this.category          = category;
            this.triggeredKeyword  = triggeredKeyword;
            this.emergencyResponse = emergencyResponse;
        }
 
        public static SafetyCheckResult safe() {
            return new SafetyCheckResult(false, null, null, null);
        }
 
        public static SafetyCheckResult emergency(String category,
                                                   String triggeredKeyword,
                                                   EmergencyResponse emergencyResponse) {
            return new SafetyCheckResult(true, category, triggeredKeyword, emergencyResponse);
        }
 
        public boolean isEmergency()                    { return emergency; }
        public String getCategory()                     { return category; }
        public String getTriggeredKeyword()             { return triggeredKeyword; }
        public EmergencyResponse getEmergencyResponse() { return emergencyResponse; }
    }
 
    public static class EmergencyResponse {
        private final String title;
        private final String message;
        private final List<EmergencyContact> contacts;
        private final boolean showCrisisResources;
 
        public EmergencyResponse(String title, String message,
                                  List<EmergencyContact> contacts, boolean showCrisisResources) {
            this.title               = title;
            this.message             = message;
            this.contacts            = contacts;
            this.showCrisisResources = showCrisisResources;
        }
 
        public String getTitle()                    { return title; }
        public String getMessage()                  { return message; }
        public List<EmergencyContact> getContacts() { return contacts; }
        public boolean isShowCrisisResources()      { return showCrisisResources; }
    }
 
    public static class EmergencyContact {
        private final String name;
        private final String number;
        private final String description;
 
        public EmergencyContact(String name, String number, String description) {
            this.name        = name;
            this.number      = number;
            this.description = description;
        }
 
        public String getName()        { return name; }
        public String getNumber()      { return number; }
        public String getDescription() { return description; }
    }
}
 