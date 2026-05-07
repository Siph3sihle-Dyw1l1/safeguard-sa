package com.safeguardsa.config;

import com.safeguardsa.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author S DYWILI
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AppUserService appUserService;   // your DB-backed UserDetailsService

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(appUserService)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/static/**", "/css/**", "/js/**",
                        "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/index", "/map", "/chat",
                        "/exit", "/login", "/tip", "/tip/submit",
                        "/chat/ask", "/emergency").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true) // ← FIXED
                .failureUrl("/login?error=true")
                .permitAll()
                )
                .exceptionHandling(ex -> ex
                .accessDeniedPage("/") // ← non-admins go home, not to login
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                );

        return http.build();
    }

}
