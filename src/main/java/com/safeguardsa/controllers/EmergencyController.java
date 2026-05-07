package com.safeguardsa.controllers;
 
import com.safeguardsa.services.SafetyGateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
import java.util.Arrays;
import java.util.List;
 
/**
 * EmergencyController - Serves the Safety Gate emergency page.
 *
 * GET /emergency → displays emergency numbers based on category.
 * Always publicly accessible — no authentication required.
 * No AI-generated content ever appears on this page.
 *
 * @author mmaphutijkgomo
 */
@Controller
public class EmergencyController {
 
    @GetMapping("/emergency")
    public String showEmergencyPage(
            @RequestParam(name = "category", defaultValue = "IMMEDIATE_DANGER") String category,
            Model model) {
 
        SafetyGateService.EmergencyResponse response = getResponseByCategory(category);
 
        model.addAttribute("title",              response.getTitle());
        model.addAttribute("message",            response.getMessage());
        model.addAttribute("contacts",           response.getContacts());
        model.addAttribute("showCrisisResources",response.isShowCrisisResources());
        model.addAttribute("category",           category);
 
        if ("MENTAL_HEALTH_CRISIS".equals(category)) {
            model.addAttribute("additionalResources", getMentalHealthResources());
        }
 
        return "emergency";
    }
 
    private SafetyGateService.EmergencyResponse getResponseByCategory(String category) {
        switch (category) {
 
            case "MENTAL_HEALTH_CRISIS":
                return new SafetyGateService.EmergencyResponse(
                        "Mental Health Crisis Support",
                        "You are not alone, and help is available right now.",
                        Arrays.asList(
                                new SafetyGateService.EmergencyContact("SADAG 24hr Helpline",  "0800 567 567", "Free, confidential 24/7 counselling"),
                                new SafetyGateService.EmergencyContact("Suicide Crisis Line",   "0800 567 567", "Immediate support for suicidal thoughts"),
                                new SafetyGateService.EmergencyContact("LifeLine SA",          "0861 322 322", "24/7 crisis intervention"),
                                new SafetyGateService.EmergencyContact("Cipla Mental Health",  "0800 456 789", "Free mental health counselling"),
                                new SafetyGateService.EmergencyContact("Police Emergency",     "10111",        "SAPS — if you are in immediate danger"),
                                new SafetyGateService.EmergencyContact("All Emergencies",      "112",          "Cell phone emergency line (free)")
                        ),
                        true
                );
 
            case "PHYSICAL_EMERGENCY":
                return new SafetyGateService.EmergencyResponse(
                        "Medical Emergency",
                        "This sounds like a medical emergency. Please call emergency services right away.",
                        Arrays.asList(
                                new SafetyGateService.EmergencyContact("National Ambulance", "10177",        "Government ambulance service"),
                                new SafetyGateService.EmergencyContact("All Emergencies",   "112",          "Free from any cell phone"),
                                new SafetyGateService.EmergencyContact("Police Emergency",  "10111",        "SAPS emergency line"),
                                new SafetyGateService.EmergencyContact("Netcare 911",       "082 911",      "Private ambulance"),
                                new SafetyGateService.EmergencyContact("Poison Helpline",   "0861 555 777", "For poisoning emergencies")
                        ),
                        false
                );
 
            case "ASSAULT_VIOLENCE":
                return new SafetyGateService.EmergencyResponse(
                        "Violence and Assault Support",
                        "Your safety is the most important thing right now.",
                        Arrays.asList(
                                new SafetyGateService.EmergencyContact("Police Emergency",               "10111",        "SAPS — for immediate help"),
                                new SafetyGateService.EmergencyContact("All Emergencies",               "112",          "Cell phone emergency (free)"),
                                new SafetyGateService.EmergencyContact("Stop Gender Violence Helpline", "0800 150 150", "Free, 24/7 support"),
                                new SafetyGateService.EmergencyContact("Childline SA",                  "0800 055 555", "For children and young people"),
                                new SafetyGateService.EmergencyContact("Lifeline GBV Support",          "0800 428 428", "Gender-based violence support")
                        ),
                        false
                );
 
            case "IMMEDIATE_DANGER":
            default:
                return new SafetyGateService.EmergencyResponse(
                        "Emergency Assistance Required",
                        "This appears to be an emergency. Please contact emergency services immediately.",
                        Arrays.asList(
                                new SafetyGateService.EmergencyContact("Police Emergency", "10111",   "SAPS emergency response"),
                                new SafetyGateService.EmergencyContact("All Emergencies", "112",     "Universal emergency (free from cell)"),
                                new SafetyGateService.EmergencyContact("Ambulance",       "10177",   "Medical emergency"),
                                new SafetyGateService.EmergencyContact("Netcare 911",     "082 911", "Private ambulance"),
                                new SafetyGateService.EmergencyContact("ER24",            "084 124", "Private emergency")
                        ),
                        false
                );
        }
    }
 
    private List<String> getMentalHealthResources() {
        return Arrays.asList(
                "It's okay to not be okay. Reaching out for help is a sign of strength.",
                "Crisis counsellors are trained to help and are available 24/7.",
                "You don't have to face this alone — support is just a phone call away.",
                "If you're at university, your campus health center can also provide support."
        );
    }
}
 