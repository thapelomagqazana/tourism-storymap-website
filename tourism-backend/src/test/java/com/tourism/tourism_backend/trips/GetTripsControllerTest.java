package com.tourism.tourism_backend.trips;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Trip;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetTripsControllerTest {

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
    private ObjectMapper objectMapper = new ObjectMapper();

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
        userRepository.deleteAll();
        tripRepository.deleteAll();
        attractionRepository.deleteAll();

        // Create admin user
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123", "ADMIN");

        // Create regular user
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
     * TC_POS_01: Retrieve all trips with valid admin token.
     */
    @Test
    public void testRetrieveAllTrips_AdminToken() throws Exception {
        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Eiffel Tower", "Famous tower in Paris", 25.0, List.of("url1", "url2")));
        Attraction attraction2 = attractionRepository.save(new Attraction("Colosseum", "Ancient Roman amphitheater", 15.0, List.of("url3", "url4")));

        // Add sample trips
        tripRepository.save(new Trip("3-Day Adventure", List.of("Day 1", "Day 2", "Day 3"), List.of(attraction1, attraction2)));
        tripRepository.save(new Trip("5-Day Tour", List.of("Day 1", "Day 2", "Day 3", "Day 4", "Day 5"), List.of(attraction1)));

        String response = mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Trip> trips = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(trips).hasSize(2);
        assertThat(trips.get(0).getName()).isEqualTo("3-Day Adventure");
        assertThat(trips.get(1).getName()).isEqualTo("5-Day Tour");
    }

    /**
     * TC_POS_02: Retrieve all trips with valid user token.
     */
    @Test
    public void testRetrieveAllTrips_UserToken() throws Exception {
        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Great Wall of China", "Historic wall in China", 10.0, List.of("url5", "url6")));

        // Add sample trips
        tripRepository.save(new Trip("1-Day Trip", List.of("Day 1"), List.of(attraction1)));

        String response = mockMvc.perform(get("/api/trips")
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Trip> trips = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(trips).hasSize(1);
        assertThat(trips.get(0).getName()).isEqualTo("1-Day Trip");
    }

    /**
     * TC_POS_03: Retrieve trips when no trips exist in the DB.
     */
    @Test
    public void testRetrieveTrips_NoTripsExist() throws Exception {
        String response = mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Trip> trips = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(trips).isEmpty();
    }

    /**
     * TC_POS_04: Retrieve trips with various durations.
     */
    @Test
    public void testRetrieveTrips_VariousDurations() throws Exception {
        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Statue of Liberty", "Iconic statue in New York", 20.0, List.of("url7")));
        Attraction attraction2 = attractionRepository.save(new Attraction("Taj Mahal", "Famous monument in India", 10.0, List.of("url8")));

        // Add sample trips with various durations
        tripRepository.save(new Trip("2-Day Trip", List.of("Day 1", "Day 2"), List.of(attraction1)));
        tripRepository.save(new Trip("7-Day Tour", List.of("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7"), List.of(attraction2)));

        String response = mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Trip> trips = objectMapper.readValue(response, new TypeReference<>() {});

        assertThat(trips).hasSize(2);
        assertThat(trips.get(0).getName()).isEqualTo("2-Day Trip");
        assertThat(trips.get(1).getName()).isEqualTo("7-Day Tour");
    }

    /**
     * TC_NEG_01: Retrieve trips with missing authorization header.
     */
    @Test
    public void testRetrieveTrips_MissingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/trips")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_02: Retrieve trips with invalid token.
     */
    @Test
    public void testRetrieveTrips_InvalidToken() throws Exception {
        String invalidToken = "Bearer invalid.token.value";

        mockMvc.perform(get("/api/trips")
                .header("Authorization", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_EDGE_01: Retrieve trips with a large number of trips in DB.
     */
    @Test
    public void testRetrieveTrips_LargeNumberOfTrips() throws Exception {
        for (int i = 1; i <= 1000; i++) {
            Trip trip = new Trip("Trip " + i, List.of("Day 1", "Day 2"), List.of());
            tripRepository.save(trip);
        }

        mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1000)); // Assert 1000 trips returned
    }

    /**
     * TC_EDGE_02: Retrieve trips with special characters in trip names.
     */
    @Test
    public void testRetrieveTrips_SpecialCharactersInNames() throws Exception {
        Trip trip = new Trip("@#$%^&*()_+{}:\"<>?", List.of("Day 1"), List.of());
        tripRepository.save(trip);

        mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("@#$%^&*()_+{}:\"<>?")); // Assert trip name matches
    }

    /**
     * TC_EDGE_03: Retrieve trips with empty arrays for days and attractions.
     */
    @Test
    public void testRetrieveTrips_EmptyDaysAndAttractions() throws Exception {
        Trip trip = new Trip("Empty Trip", List.of(), List.of());
        tripRepository.save(trip);

        mockMvc.perform(get("/api/trips")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].days").isEmpty()) // Assert empty days
                .andExpect(jsonPath("$[0].attractions").isEmpty()); // Assert empty attractions
    }
}
