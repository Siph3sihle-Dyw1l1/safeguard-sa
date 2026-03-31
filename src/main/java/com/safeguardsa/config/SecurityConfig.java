package com.safeguardsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. THE ASSETS: Allow the CSS/Images/Favicons first (Stops the 403)
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // 2. THE PUBLIC ROUTES: Explicitly permit these paths (Stops the Redirect Loop)
                .requestMatchers("/", "/index", "/map", "/chat", "/exit", "/login").permitAll()
                
                // 3. THE SECURE ZONE: Everything else (Dashboard, Admin) needs login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")        // Points to your custom login URL
                .loginProcessingUrl("/login") // Matches the form action
                .defaultSuccessUrl("/", true)
                .permitAll()               // CRITICAL: Allows the login page to be seen by the public
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}