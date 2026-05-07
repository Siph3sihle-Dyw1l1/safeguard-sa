package com.safeguardsa.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mmaphutijkgomo
 */
@Configuration
public class SpringAIConfig {

    @Autowired
    private VertexAiGeminiChatModel chatModel;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are SafeGuard SA's Health Assistant — a helpful, compassionate AI
                        designed to answer general health questions for South African university students.
                        Never diagnose. Always recommend seeing a doctor for personal concerns.
                        """)
                .build();
    }
}