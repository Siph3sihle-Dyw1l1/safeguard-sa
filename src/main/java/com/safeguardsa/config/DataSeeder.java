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
            String seedPassword = System.getenv("SEED_ADMIN_PASSWORD");

            if (!appUserService.isUsernameTaken("admin1")) {
                if (seedPassword != null && !seedPassword.isEmpty()) {
                    appUserService.registerAdmin(
                            "admin1",
                            seedPassword,
                            "admin@safeguard.co.za"
                    );
                    System.out.println(">>> DataSeeder: admin1 created successfully");
                } else {
                    System.out.println(">>> DataSeeder: SEED_ADMIN_PASSWORD environment variable is missing!");
                }
            } else {
                System.out.println(">>> DataSeeder: admin1 already exists, skipping");
            }
        };
    }
}
