package com.safeguardsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.User;           // ADDED THIS
import org.springframework.security.core.userdetails.UserDetails;    // ADDED THIS
import org.springframework.security.core.userdetails.UserDetailsService; // ADDED THIS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager; // ADDED THIS

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
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
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ← no deprecation warning
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user1 = User.builder()
                .username("doctor1")
                .password(encoder.encode("SafeGuard2026"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin1")
                .password(encoder.encode("Admin2026"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }
}
