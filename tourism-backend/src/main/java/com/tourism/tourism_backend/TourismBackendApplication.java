package com.tourism.tourism_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Tourism Story Map backend.
 */
@SpringBootApplication
@EnableScheduling
public class TourismBackendApplication {

    public static void main(String[] args) {
        // Determine the environment file based on the system property "TEST_ENV"
        String envFile = System.getProperty("TEST_ENV", ".env"); // Defaults to .env

        // Load environment variables from the specified file
        Dotenv dotenv = Dotenv.configure().filename(envFile).load();

        // Set system properties to allow Spring Boot to access them
        System.setProperty("server.port", dotenv.get("SERVER_PORT"));
        System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        System.setProperty("jwt.expiration.ms", dotenv.get("JWT_EXPIRATION_MS"));

        // Start the Spring Boot application
        SpringApplication.run(TourismBackendApplication.class, args);
    }
}
