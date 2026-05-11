package com.safeguardsa.services;

import com.safeguardsa.models.ChatMessage;
import com.safeguardsa.models.ChatSession;
import com.safeguardsa.repositories.ChatMessageRepository;
import com.safeguardsa.repositories.ChatSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * @author mmaphutijkgomo
 */
@Service
public class ChatService {

    private final SafetyGateService safetyGateService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;

    @Autowired
    public ChatService(SafetyGateService safetyGateService,
            ChatMessageRepository chatMessageRepository,
            ChatSessionRepository chatSessionRepository,
            ObjectMapper objectMapper,
            ChatClient.Builder chatClientBuilder) { // ✅ Fixed: Uses Builder

        this.safetyGateService = safetyGateService;
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.objectMapper = objectMapper;

        // ✅ Fixed: Builds the ChatClient correctly
        this.chatClient = chatClientBuilder
                .defaultSystem("You are the SafeGuard SA AI Medic. Provide calm, concise, and professional first-aid advice to students.")
                .build();
    }

    public ChatResponse processMessage(String userMessage, String sessionToken)
            throws Exception {

        // Step 1: Safety Gate
        SafetyGateService.SafetyCheckResult safetyResult
                = safetyGateService.checkMessage(userMessage);

        if (safetyResult.isEmergency()) {
            saveMessage(sessionToken, userMessage, null, true);

            return ChatResponse.emergency(
                    safetyResult.getCategory(),
                    safetyResult.getTriggeredKeyword(),
                    safetyResult.getEmergencyResponse()
            );
        }

        // Step 2: AI response (Spring AI handles everything)
        String aiResponse;

        try {
            aiResponse = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();

        } catch (Exception e) {
            System.err.println("[ChatService] FULL ERROR:");
            e.printStackTrace();
            aiResponse = "I'm having trouble connecting right now. Please try again.";
        }

        // Step 3: Save conversation
        saveMessage(sessionToken, userMessage, aiResponse, false);

        return ChatResponse.success(aiResponse, Collections.emptyList());
    }

    private void saveMessage(String sessionToken, String userQuestion,
            String aiResponse, boolean isEmergency) {

        try {
            String token = sessionToken != null ? sessionToken : UUID.randomUUID().toString();

            ChatSession session = chatSessionRepository
                    .findBySessionToken(token)
                    .orElseGet(() -> {
                        ChatSession s = new ChatSession();
                        s.setSessionToken(token);
                        s.setCreatedAt(LocalDateTime.now());
                        s.setLastActiveAt(LocalDateTime.now());
                        return chatSessionRepository.save(s);
                    });

            session.setLastActiveAt(LocalDateTime.now());
            chatSessionRepository.save(session);

            ChatMessage userMsg = new ChatMessage();
            userMsg.setSession(session);
            userMsg.setRole("USER");
            userMsg.setContent(userQuestion);
            userMsg.setEmergency(isEmergency);
            userMsg.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(userMsg);

            if (aiResponse != null) {
                ChatMessage botMsg = new ChatMessage();
                botMsg.setSession(session);
                botMsg.setRole("BOT");
                botMsg.setContent(aiResponse);
                botMsg.setEmergency(false);
                botMsg.setCreatedAt(LocalDateTime.now());
                chatMessageRepository.save(botMsg);
            }

        } catch (Exception e) {
            System.err.println("[ChatService] Failed to save message: " + e.getMessage());
        }
    }

    public static class ChatResponse {

        private final boolean emergency;
        private final String response;
        private final java.util.List<String> sources;
        private final String category;
        private final String triggeredKeyword;
        private final SafetyGateService.EmergencyResponse emergencyResponse;

        private ChatResponse(boolean emergency, String response,
                java.util.List<String> sources,
                String category,
                String triggeredKeyword,
                SafetyGateService.EmergencyResponse emergencyResponse) {

            this.emergency = emergency;
            this.response = response;
            this.sources = sources;
            this.category = category;
            this.triggeredKeyword = triggeredKeyword;
            this.emergencyResponse = emergencyResponse;
        }

        public static ChatResponse success(String response, java.util.List<String> sources) {
            return new ChatResponse(false, response, sources, null, null, null);
        }

        public static ChatResponse emergency(String category, String triggeredKeyword,
                SafetyGateService.EmergencyResponse emergencyResponse) {
            return new ChatResponse(true, null, null, category, triggeredKeyword, emergencyResponse);
        }

        public boolean isEmergency() {
            return emergency;
        }

        public String getResponse() {
            return response;
        }

        public java.util.List<String> getSources() {
            return sources;
        }

        public String getCategory() {
            return category;
        }

        public String getTriggeredKeyword() {
            return triggeredKeyword;
        }

        public SafetyGateService.EmergencyResponse getEmergencyResponse() {
            return emergencyResponse;
        }
    }
}
