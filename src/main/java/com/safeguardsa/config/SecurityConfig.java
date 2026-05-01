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
import org.springframework.security.provisioning.InMemoryUserDetailsManager; // ADDED THIS

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
                
                // 3. Only ADMIN users can access anything inside /admin (e.g. /admin/dashboard)
                // Regular users (USER role) will be blocked    
                .requestMatchers("/admin/**").hasRole("ADMIN")
                    
                // 4. THE SECURE ZONE: Everything else (Dashboard, Admin) needs login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")      // Points to your custom login URL
                .loginProcessingUrl("/login") // Matches the form action
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")  // QA: easier to test failed login
                .permitAll()               // CRITICAL: Allows the login page to be seen by the public
            )
                .exceptionHandling(ex -> ex
        .accessDeniedPage("/login")
    )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/?logout=true") // QA: confirm logout worked
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
    // QA TEST USER 
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("doctor1")
                .password("SafeGuard2026")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin1") 
                .password("***REMOVED***")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }
}