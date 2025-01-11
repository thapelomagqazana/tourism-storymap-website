package com.tourism.tourism_backend.attractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AttractionTrafficControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken;

    private Attraction attraction1;
    private Attraction attraction2;
    private Attraction attraction3;

    @BeforeEach
    public void setup() throws Exception {
        attractionRepository.deleteAll();
        userRepository.deleteAll();

        // Add initial attractions to the database
        attraction1 = attractionRepository.save(new Attraction("Eiffel Tower", "Famous tower in Paris", 25.0, null));
        attraction2 = attractionRepository.save(new Attraction("Colosseum", "Ancient Roman amphitheater", 15.0, null));
        attraction3 = attractionRepository.save(new Attraction("Great Wall of China", "Ancient wall in China", 10.0, null));

        // Create admin user
        AppUser adminUser = new AppUser("Admin", "admin@example.com",
                new BCryptPasswordEncoder().encode("admin123"), "ADMIN");
        userRepository.save(adminUser);

        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");
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
     * TC_POS_01: Increment traffic count for an existing attraction.
     */
    @Test
    public void testIncrementTrafficCount_ExistingAttraction() throws Exception {
        mockMvc.perform(post("/api/attractions/" + attraction1.getId() + "/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trafficCount").value(1));
    }

    /**
     * TC_POS_02: Increment traffic count for an attraction with 0 traffic count.
     */
    @Test
    public void testIncrementTrafficCount_ZeroTraffic() throws Exception {
        mockMvc.perform(post("/api/attractions/" + attraction2.getId() + "/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trafficCount").value(1));
    }

    /**
     * TC_POS_03: Increment traffic count multiple times in a row.
     */
    @Test
    public void testIncrementTrafficCount_MultipleRequests() throws Exception {
        for (int i = 1; i <= 3; i++) {
            mockMvc.perform(post("/api/attractions/" + attraction1.getId() + "/traffic")
                    .header("Authorization", adminJwtToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.trafficCount").value(i));
        }
    }

    /**
     * TC_POS_04: Increment traffic count for a newly added attraction.
     */
    @Test
    public void testIncrementTrafficCount_NewAttraction() throws Exception {
        // Add a new attraction
        Attraction newAttraction = attractionRepository.save(new Attraction("Statue of Liberty", "Iconic statue in New York", 20.0, null));

        mockMvc.perform(post("/api/attractions/" + newAttraction.getId() + "/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trafficCount").value(1));
    }

    /**
     * TC_NEG_01: Increment traffic count for non-existent attraction.
     */
    @Test
    public void testIncrementTrafficCount_NonExistentAttraction() throws Exception {
        mockMvc.perform(post("/api/attractions/9999/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9999"));
    }

    /**
     * TC_NEG_02: Increment traffic count with unauthorized user (No Authorization header).
     */
    @Test
    public void testIncrementTrafficCount_UnauthorizedUser() throws Exception {
        mockMvc.perform(post("/api/attractions/" + attraction1.getId() + "/traffic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_03: Increment traffic count with malformed token.
     */
    @Test
    public void testIncrementTrafficCount_MalformedToken() throws Exception {
        mockMvc.perform(post("/api/attractions/" + attraction1.getId() + "/traffic")
                .header("Authorization", "Bearer malformed.token.value")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_EDGE_01: Increment traffic count for an attraction with ID = 0.
     */
    @Test
    public void testIncrementTrafficCount_IdZero() throws Exception {
        mockMvc.perform(post("/api/attractions/0/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 0"));
    }

    /**
     * TC_EDGE_02: Increment traffic count for an attraction with negative ID.
     */
    @Test
    public void testIncrementTrafficCount_NegativeId() throws Exception {
        mockMvc.perform(post("/api/attractions/-1/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: -1"));
    }

    /**
     * TC_EDGE_03: Increment traffic count for an attraction with very large ID.
     */
    @Test
    public void testIncrementTrafficCount_LargeId() throws Exception {
        mockMvc.perform(post("/api/attractions/9223372036854775807/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9223372036854775807"));
    }

    /**
     * TC_EDGE_04: Increment traffic count for an attraction with special characters in ID.
     */
    // @Test
    // public void testIncrementTrafficCount_SpecialCharacterId() throws Exception {
    //     mockMvc.perform(post("/api/attractions/@!#$%/traffic")
    //             .header("Authorization", adminJwtToken)
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.error").value("Invalid ID"));
    // }

    /**
     * TC_EDGE_05: Increment traffic count while the database is empty.
     */
    @Test
    public void testIncrementTrafficCount_EmptyDatabase() throws Exception {
        attractionRepository.deleteAll();

        mockMvc.perform(post("/api/attractions/1/traffic")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 1"));
    }
}
