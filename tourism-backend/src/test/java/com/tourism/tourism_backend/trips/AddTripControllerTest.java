package com.tourism.tourism_backend.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.TripRepository;
import com.tourism.tourism_backend.repositories.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeAll;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken;
    private String userJwtToken;
    private List<Long> attractionIds;

    @BeforeAll
    static void setUp() {
        // Load the .env.test file
        String envFile = System.getProperty("TEST_ENV", ".env.test");
        Dotenv dotenv = Dotenv.configure().filename(envFile).load();

        // Set system properties for testing
        System.setProperty("server.port", dotenv.get("SERVER_PORT"));
        System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        System.setProperty("jwt.expiration.ms", dotenv.get("JWT_EXPIRATION_MS"));
    }

    @BeforeEach
    public void setup() throws Exception {
        tripRepository.deleteAll();
        attractionRepository.deleteAll();

        // Create sample attractions
        Attraction attraction1 = new Attraction("Eiffel Tower", "Famous tower in Paris", 25.0, List.of("url1", "url2"));
        Attraction attraction2 = new Attraction("Colosseum", "Ancient Roman amphitheater", 15.0, List.of());
        Attraction attraction3 = new Attraction("Statue of Liberty", "Iconic statue in New York", 20.0, List.of());
        List<Attraction> savedAttractions = attractionRepository.saveAll(List.of(attraction1, attraction2, attraction3));

        // Retrieve the IDs of saved attractions
        attractionIds = savedAttractions.stream().map(Attraction::getId).collect(Collectors.toList());

        // Create an admin user and get their token
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

    /**
     * TC_POS_01: Add a trip with valid data and attractions.
     */
    @Test
    public void testAddTrip_ValidData() throws Exception {
        String requestBody = """
            {
                "name": "Weekend Getaway",
                "duration": ["Day 1: Arrival", "Day 2: Sightseeing", "Day 3: Departure"],
                "attractionIds": [%d, %d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1), attractionIds.get(2));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_POS_02: Add a trip with a single attraction.
     */
    @Test
    public void testAddTrip_SingleAttraction() throws Exception {
        String requestBody = """
            {
                "name": "Solo Adventure",
                "duration": ["Day 1: Solo exploration"],
                "attractionIds": [%d]
            }
        """.formatted(attractionIds.get(0));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_POS_03: Add a trip with no attractions (empty array).
     */
    @Test
    public void testAddTrip_NoAttractions() throws Exception {
        String requestBody = """
            {
                "name": "Relaxation Retreat",
                "duration": ["Day 1: Check-in", "Day 2: Relaxation"],
                "attractionIds": []
            }
        """;

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_POS_04: Add a trip with the maximum allowed name length (255 characters).
     */
    @Test
    public void testAddTrip_MaxNameLength() throws Exception {
        String longName = "A".repeat(255);
        String requestBody = """
            {
                "name": "%s",
                "duration": ["Day 1: Adventure starts", "Day 2: Full day tour"],
                "attractionIds": [%d, %d]
            }
        """.formatted(longName, attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_POS_05: Add a trip with a valid admin token.
     */
    @Test
    public void testAddTrip_ValidAdminToken() throws Exception {
        String requestBody = """
            {
                "name": "Nature Tour",
                "duration": ["Day 1: Forest hike", "Day 2: River rafting", "Day 3: Departure"],
                "attractionIds": [%d, %d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1), attractionIds.get(2));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_NEG_02: Add a trip with invalid duration (negative value).
     */
    @Test
    public void testAddTrip_InvalidDuration() throws Exception {
        String requestBody = """
            {
                "name": "Invalid Trip",
                "duration": -3,
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    /**
     * TC_NEG_03: Add a trip with non-existent attraction IDs.
     */
    @Test
    public void testAddTrip_NonExistentAttractions() throws Exception {
        String requestBody = """
            {
                "name": "Non-Existent Attractions",
                "duration": ["Day 1", "Day 2", "Day 3"],
                "attractionIds": [9999, 8888]
            }
        """;

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9999"));
    }

    /**
     * TC_NEG_04: Add a trip without an authorization header.
     */
    @Test
    public void testAddTrip_NoAuthorizationHeader() throws Exception {
        String requestBody = """
            {
                "name": "Unauthorized Trip",
                "duration": ["Day 1", "Day 2", "Day 3"],
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_05: Add a trip with an invalid token.
     */
    @Test
    public void testAddTrip_InvalidToken() throws Exception {
        String requestBody = """
            {
                "name": "Invalid Token Trip",
                "duration": ["Day 1", "Day 2", "Day 3"],
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_NEG_06: Add a trip with a non-admin token.
     */
    @Test
    public void testAddTrip_NonAdminToken() throws Exception {
        String requestBody = """
            {
                "name": "Unauthorized Access Trip",
                "duration": ["Day 1", "Day 2", "Day 3"],
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));

        String userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123"); // Assuming a non-admin user exists

        mockMvc.perform(post("/api/trips")
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"));
    }

    /**
     * TC_EDGE_01: Add a trip with an empty attractions array.
     */
    @Test
    public void testAddTrip_EmptyAttractions() throws Exception {
        String requestBody = """
            {
                "name": "Empty Attractions",
                "duration": ["Day 1", "Day 2"],
                "attractionIds": []
            }
        """;

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_EDGE_02: Add a trip with maximum integer duration.
     */
    @Test
    public void testAddTrip_MaximumDuration() throws Exception {
        String requestBody = """
            {
                "name": "Maximum Duration Trip",
                "duration": ["Day 1"],
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }

    /**
     * TC_EDGE_03: Add a trip with duplicate attraction IDs.
     */
    @Test
    public void testAddTrip_DuplicateAttractionIds() throws Exception {
        String requestBody = """
            {
                "name": "Duplicate Attractions",
                "duration": ["Day 1", "Day 2", "Day 3"],
                "attractionIds": [%d, %d, %d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(0), attractionIds.get(1), attractionIds.get(1));

        mockMvc.perform(post("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Trip created successfully"));
    }
}