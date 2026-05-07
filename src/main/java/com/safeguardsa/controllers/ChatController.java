package com.safeguardsa.controllers;

import com.safeguardsa.services.SafetyGateService;
import com.safeguardsa.services.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ChatController - Handles AI health chatbot endpoints.
 *
 * GET /chat → serves chat.html POST /chat/ask → processes health question
 * through Safety Gate + RAG + Gemini
 *
 * @author mmaphutijkgomo
 */
@Controller
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Serves the chatbot UI page.
     *
     * @return
     */
    @GetMapping("/chat")
    public String chatPage() {
        return "forward:/chat.html";
    }

    /**
     * Processes a student health question.Request: { "message": "What are
     * symptoms of dehydration?" } Response: { "emergency": false, "response":
     * "...", "sources": [...] } or Response: { "emergency": true, "category":
     * "...", "title": "...", "emergencyMessage": "...", "contacts": [...] }
     *
     * @param body
     * @param httpSession
     * @return
     */
    @PostMapping("/chat/ask")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ask(
            @RequestBody Map<String, String> body,
            HttpSession httpSession) {

        Map<String, Object> response = new HashMap<>();

        String message = body.get("message");

        if (message == null || message.trim().isEmpty()) {
            response.put("error", true);
            response.put("message", "Please enter a message.");
            return ResponseEntity.badRequest().body(response);
        }

        if (message.length() > 500) {
            response.put("error", true);
            response.put("message", "Message too long. Please keep it under 500 characters.");
            return ResponseEntity.badRequest().body(response);
        }

        // Get or create anonymous session token
        String sessionToken = (String) httpSession.getAttribute("sessionToken");
        if (sessionToken == null) {
            sessionToken = UUID.randomUUID().toString();
            httpSession.setAttribute("sessionToken", sessionToken);
        }

        try {
            ChatService.ChatResponse chatResponse
                    = chatService.processMessage(message.trim(), sessionToken);

            if (chatResponse.isEmergency()) {
                response.put("emergency", true);
                response.put("category", chatResponse.getCategory());
                response.put("triggeredKeyword", chatResponse.getTriggeredKeyword());

                SafetyGateService.EmergencyResponse er = chatResponse.getEmergencyResponse();
                if (er != null) {
                    response.put("title", er.getTitle());
                    response.put("emergencyMessage", er.getMessage());
                    response.put("contacts", er.getContacts());
                }
            } else {
                response.put("emergency", false);
                response.put("response", chatResponse.getResponse());
                response.put("sources", chatResponse.getSources());
            }

        } catch (Exception e) {
            response.put("emergency", false);
            response.put("error", true);
            response.put("response", "I'm having trouble right now. Please try again. "
                    + "For urgent help call 10177 or 112.");
            System.err.println("[ChatController] Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     * @return 
     */
    @GetMapping("/chat/health")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("service", "chat");
        health.put("safetyGate", "active");
        health.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(health);
    }
}
