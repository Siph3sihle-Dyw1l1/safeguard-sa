package com.safeguardsa.config;

import com.safeguardsa.services.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author S DYWILI
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(AppUserService appUserService) {
        return args -> {

            // Only creates admin1 if it doesn't already exist
            // Safe to run on every restart — no duplicate errors
            if (!appUserService.isUsernameTaken("admin1")) {
                appUserService.registerAdmin(
                        "admin1",
                        "***REMOVED***",
                        "admin@safeguard.co.za"
                );
                System.out.println(">>> DataSeeder: admin1 created successfully");
            } else {
                System.out.println(">>> DataSeeder: admin1 already exists, skipping");
            }

        };
    }
}
