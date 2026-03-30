package com.safeguardsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*@author S DYWILI
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                    //Allow everyone to see these pages
                    .requestMatchers("/","/map","/tip/**","/chat/**","/emergency",
                            "/exit","/css/**","/js/**").permitAll()
                    //The most important one (Allow only the admins to see the dashboard
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.permitAll());
        return http.build();
    }
}
