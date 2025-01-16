package com.tourism.tourism_backend.attractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetAttractionByIdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;
    private Long attractionId1;
    private Long attractionId2;
    private Long attractionId3;
    private Long attractionId4;

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
        attractionRepository.deleteAll();

        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Attraction 1", "Description 1",  100.0, List.of("url1")));
        Attraction attraction2 = attractionRepository.save(new Attraction("Attraction 2", "Description 2", 120.0, List.of("url1", "url2")));
        Attraction attraction3 = attractionRepository.save(new Attraction("Attraction 3", "Description 3", 150.0, List.of()));
        Attraction attraction4 = attractionRepository.save(new Attraction("Attraction 4", "D".repeat(500), 200.0,  List.of("url1")));
        attractionRepository.save(new Attraction("Sample Attraction", "Sample Description", 200.0,  List.of("url1", "url2")));

        // Obtain their IDs for use in the tests
        attractionId1 = attraction1.getId();
        attractionId2 = attraction2.getId();
        attractionId3 = attraction3.getId();
        attractionId4 = attraction4.getId();
        

        // Create a sample user for login
        AppUser user = new AppUser("Test User", "test.user@example.com", new BCryptPasswordEncoder().encode("password123"));
        userRepository.save(user);

        // Obtain JWT token
        MvcResult result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test.user@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = "Bearer " + new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    /**
     * TC_POS_01: Retrieve an attraction by a valid ID.
     */
    @Test
    public void testRetrieveAttractionById_ValidId() throws Exception {
        mockMvc.perform(get("/api/attractions/" + attractionId1)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Attraction 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.photos.length()").value(1));
    }

    /**
     * TC_POS_02: Retrieve an attraction with multiple photos.
     */
    @Test
    public void testRetrieveAttractionById_MultiplePhotos() throws Exception {
        mockMvc.perform(get("/api/attractions/" + attractionId2)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Attraction 2"))
                .andExpect(jsonPath("$.description").value("Description 2"))
                .andExpect(jsonPath("$.photos.length()").value(2))
                .andExpect(jsonPath("$.photos[0]").value("url1"))
                .andExpect(jsonPath("$.photos[1]").value("url2"));
    }

    /**
     * TC_POS_03: Retrieve an attraction with no photos.
     */
    @Test
    public void testRetrieveAttractionById_NoPhotos() throws Exception {
        mockMvc.perform(get("/api/attractions/" + attractionId3)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Attraction 3"))
                .andExpect(jsonPath("$.description").value("Description 3"))
                .andExpect(jsonPath("$.photos.length()").value(0));
    }

    /**
     * TC_POS_04: Retrieve an attraction with maximum description length.
     */
    @Test
    public void testRetrieveAttractionById_MaxDescriptionLength() throws Exception {
        mockMvc.perform(get("/api/attractions/" + attractionId4)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Attraction 4"))
                // .andExpect(jsonPath("$.description.length()").value(500))
                .andExpect(jsonPath("$.photos.length()").value(1));
    }

    /**
     * TC_NEG_01: Attempt to retrieve attraction with a non-existing ID.
     */
    @Test
    public void testRetrieveAttractionById_NonExistingId() throws Exception {
        mockMvc.perform(get("/api/attractions/999")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with id: 999"));
    }

    /**
     * TC_NEG_02: Attempt to retrieve attraction with invalid ID format.
     */
    @Test
    public void testRetrieveAttractionById_InvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/attractions/abc")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid attraction ID"));
    }

    /**
     * TC_NEG_03: Attempt to retrieve attraction without providing an ID.
     */
    @Test
    public void testRetrieveAttractionById_NoIdProvided() throws Exception {
        mockMvc.perform(get("/api/attractions/")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
    }
}


