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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DeleteAttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken;
    private String userJwtToken;

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
        attractionRepository.deleteAll();

        // Create admin user
        AppUser adminUser = new AppUser("Admin", "admin@example.com",
                new BCryptPasswordEncoder().encode("admin123"), "ADMIN");
        userRepository.save(adminUser);
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");

        // Create non-admin user
        AppUser regularUser = new AppUser("User", "user@example.com",
                new BCryptPasswordEncoder().encode("user123"), "USER");
        userRepository.save(regularUser);
        userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123");

        // Add sample attractions
        attractionRepository.save(new Attraction("Eiffel Tower", "Famous tower in Paris", 25.0, null));
        attractionRepository.save(new Attraction("Colosseum", "Ancient Roman amphitheater", 15.0, null));
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
     * TC_POS_01: Delete an existing attraction by valid ID.
     */
    @Test
    public void testDeleteAttraction_ValidId() throws Exception {
        Long attractionId = attractionRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Attraction deleted successfully"));
    }

    /**
     * TC_POS_02: Delete an attraction with a valid admin token.
     */
    @Test
    public void testDeleteAttraction_ValidAdminToken() throws Exception {
        Long attractionId = attractionRepository.findAll().get(1).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Attraction deleted successfully"));
    }

    /**
     * TC_NEG_01: Delete an attraction with non-existent ID.
     */
    @Test
    public void testDeleteAttraction_NonExistentId() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", 9999)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9999"));
    }

    /**
     * TC_NEG_02: Delete an attraction with unauthorized user.
     */
    @Test
    public void testDeleteAttraction_UnauthorizedUser() throws Exception {
        Long attractionId = attractionRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId)
                .header("Authorization", userJwtToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"));
    }

    /**
     * TC_NEG_03: Delete an attraction without authorization header.
     */
    @Test
    public void testDeleteAttraction_NoAuthorizationHeader() throws Exception {
        Long attractionId = attractionRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_04: Delete an attraction with invalid token format.
     */
    @Test
    public void testDeleteAttraction_InvalidTokenFormat() throws Exception {
        Long attractionId = attractionRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId)
                .header("Authorization", "InvalidToken"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_05: Delete an attraction with malformed token.
     */
    @Test
    public void testDeleteAttraction_MalformedToken() throws Exception {
        Long attractionId = attractionRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/attractions/{id}", attractionId)
                .header("Authorization", "Bearer malformed.token.string"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_EDGE_01: Delete an attraction with ID = 0.
     */
    @Test
    public void testDeleteAttraction_IdZero() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", 0)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 0"));
    }

    /**
     * TC_EDGE_02: Delete an attraction with negative ID.
     */
    @Test
    public void testDeleteAttraction_NegativeId() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", -1)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: -1"));
    }

    /**
     * TC_EDGE_03: Delete an attraction with very large ID.
     */
    @Test
    public void testDeleteAttraction_LargeId() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", 9223372036854775807L)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9223372036854775807"));
    }

    /**
     * TC_EDGE_04: Delete an attraction with special characters in ID.
     */
    @Test
    public void testDeleteAttraction_SpecialCharactersInId() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", "@!#$")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid attraction ID"));
    }

    /**
     * TC_EDGE_05: Delete an attraction while the database is empty.
     */
    @Test
    public void testDeleteAttraction_DatabaseEmpty() throws Exception {
        mockMvc.perform(delete("/api/attractions/{id}", 1)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 1"));
    }
}
