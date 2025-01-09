package com.tourism.tourism_backend;

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
        SpringApplication.run(TourismBackendApplication.class, args);
    }
}
