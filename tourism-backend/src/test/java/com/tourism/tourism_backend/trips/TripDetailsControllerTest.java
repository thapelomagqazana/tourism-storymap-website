package com.tourism.tourism_backend.trips;

import com.tourism.tourism_backend.models.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TripDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private Long tripId1, tripId2, tripId3, tripId4, tripId5;

    private String userJwtToken;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() throws Exception {
        // Clear the database before each test
        tripRepository.deleteAll();
        attractionRepository.deleteAll();

        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1")));
        Attraction attraction2 = attractionRepository.save(new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2")));
        Attraction attraction3 = attractionRepository.save(new Attraction("Attraction 3", "Description 3", 20.0, List.of("photo3")));

        // Add sample trips and retrieve their IDs
        tripId1 = tripRepository.save(new Trip("Trip 1", List.of("Day 1", "Day 2", "Day 3"), List.of(attraction1, attraction2))).getId();
        tripId2 = tripRepository.save(new Trip("Trip 2", List.of("Day 1", "Day 2", "Day 3", "Day 4", "Day 5"), List.of(attraction1, attraction2, attraction3))).getId();
        tripId3 = tripRepository.save(new Trip("Trip 3", List.of("Day 1", "Day 2"), List.of(attraction1, attraction3))).getId();
        tripId4 = tripRepository.save(new Trip("Trip 4", List.of("Day 1", "Day 2", "Day 3"), List.of())).getId();
        tripId5 = tripRepository.save(new Trip("Trip 5", List.of(), List.of(attraction2, attraction3))).getId();

        userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123", "USER");
    }

    private String obtainJwtToken(String email, String password, String role) throws Exception {
        userRepository.save(new AppUser("Test User", email, new BCryptPasswordEncoder().encode(password), role));
        String response = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    /**
     * TC_POS_01: Retrieve trip details with a valid ID.
     */
    @Test
    public void testRetrieveTripDetails_ValidId() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip 1"))
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.attractions").isArray());
    }

    /**
     * TC_POS_02: Retrieve trip details for a trip with multiple days.
     */
    @Test
    public void testRetrieveTripDetails_MultipleDays() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId2)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip 2"))
                .andExpect(jsonPath("$.days.length()").value(5))
                .andExpect(jsonPath("$.attractions.length()").value(3));
    }

    /**
     * TC_POS_03: Retrieve trip details for a trip with multiple attractions.
     */
    @Test
    public void testRetrieveTripDetails_MultipleAttractions() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId3)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip 3"))
                .andExpect(jsonPath("$.days.length()").value(2))
                .andExpect(jsonPath("$.attractions.length()").value(2));
    }

    /**
     * TC_POS_04: Retrieve trip details for a trip with no attractions.
     */
    @Test
    public void testRetrieveTripDetails_NoAttractions() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId4)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip 4"))
                .andExpect(jsonPath("$.days.length()").value(3))
                .andExpect(jsonPath("$.attractions").isEmpty());
    }

    /**
     * TC_POS_05: Retrieve trip details for a trip with no days.
     */
    @Test
    public void testRetrieveTripDetails_NoDays() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId5)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Trip 5"))
                .andExpect(jsonPath("$.days").isEmpty())
                .andExpect(jsonPath("$.attractions.length()").value(2));
    }

    /**
     * TC_NEG_01: Retrieve trip details for a non-existent ID.
     */
    @Test
    public void testRetrieveTripDetails_NonExistentId() throws Exception {
        mockMvc.perform(get("/api/trips/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", userJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found with ID: 9999"));
    }

    /**
     * TC_NEG_02: Retrieve trip details without authorization header.
     */
    @Test
    public void testRetrieveTripDetails_NoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_03: Retrieve trip details with an invalid token.
     */
    @Test
    public void testRetrieveTripDetails_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

}
