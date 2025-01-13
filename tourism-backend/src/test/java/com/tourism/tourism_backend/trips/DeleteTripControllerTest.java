package com.tourism.tourism_backend.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.TripRepository;
import com.tourism.tourism_backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DeleteTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken, userJwtToken;
    private List<Long> tripIds;

    @BeforeEach
    public void setup() throws Exception {
        // Add sample attractions
        Attraction attraction1 = new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1"));
        Attraction attraction2 = new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2"));
        attractionRepository.saveAll(List.of(attraction1, attraction2));

        // Add sample trips and store their IDs
        Trip trip1 = tripRepository.save(new Trip("Trip 1", List.of("Day 1", "Day 2"), List.of(attraction1, attraction2)));
        Trip trip2 = tripRepository.save(new Trip("Trip 2", List.of("Day 1"), List.of(attraction1)));
        Trip trip3 = tripRepository.save(new Trip("Trip 3", List.of("Day 1", "Day 2"), List.of()));
        tripIds = List.of(trip1.getId(), trip2.getId(), trip3.getId());

        // Create admin and user accounts
        userRepository.save(new AppUser("Admin", "admin@example.com", new BCryptPasswordEncoder().encode("admin123"), "ADMIN"));
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");

        userRepository.save(new AppUser("User", "user@example.com", new BCryptPasswordEncoder().encode("user123"), "USER"));
        userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123");
    }

    private String obtainJwtToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\" }"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new ObjectMapper().readTree(response).get("token").asText();
    }

    // Positive Test Cases

    @Test
    public void testDeleteTrip_ValidId() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripIds.get(0))
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTrip_ValidAdminToken() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripIds.get(1))
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk());
    }

    // Negative Test Cases

    @Test
    public void testDeleteTrip_NonExistentId() throws Exception {
        mockMvc.perform(delete("/api/trips/9999")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTrip_NoAuthorizationHeader() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripIds.get(0)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteTrip_InvalidToken() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripIds.get(0))
                .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteTrip_NonAdminToken() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripIds.get(0))
                .header("Authorization", userJwtToken))
                .andExpect(status().isForbidden());
    }

    // Edge Test Cases

    @Test
    public void testDeleteTrip_IdZero() throws Exception {
        mockMvc.perform(delete("/api/trips/0")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found with ID: 0"));
    }

    @Test
    public void testDeleteTrip_NegativeId() throws Exception {
        mockMvc.perform(delete("/api/trips/-1")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found with ID: -1"));
    }

    // @Test
    // public void testDeleteTrip_DatabaseEmpty() throws Exception {
    //     // Clear all trips from the database
    //     tripRepository.deleteAll();

    //     mockMvc.perform(delete("/api/trips/1")
    //             .header("Authorization", adminJwtToken))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.error").value("Trip not found with ID: 1"));
    // }

    @Test
    public void testDeleteTrip_SpecialCharactersInId() throws Exception {
        mockMvc.perform(delete("/api/trips/@#$")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid attraction ID"));
    }
    
}
