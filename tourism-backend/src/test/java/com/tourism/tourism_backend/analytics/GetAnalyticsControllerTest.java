package com.tourism.tourism_backend.analytics;

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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken;
    private String userJwtToken;

    @BeforeEach
    public void setup() throws Exception {
        // Create admin and user accounts and obtain tokens
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

    @Test
    public void testRetrieveAnalytics_ValidAdminToken() throws Exception {
        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").isNumber())
                .andExpect(jsonPath("$.mostVisitedAttractions").isArray());
    }

    @Test
    public void testRetrieveAnalytics_NoAttractions() throws Exception {
        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(0))
                .andExpect(jsonPath("$.mostVisitedAttractions").isEmpty());
    }

    @Test
    public void testRetrieveAnalytics_MultipleAttractions() throws Exception {
        attractionRepository.saveAll(List.of(
                new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1"), 100),
                new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2"), 150),
                new Attraction("Attraction 3", "Description 3", 20.0, List.of("photo3"), 120)
        ));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(370))
                .andExpect(jsonPath("$.mostVisitedAttractions.length()").value(3));
    }

    @Test
    public void testRetrieveAnalytics_Top5Attractions() throws Exception {
        for (int i = 1; i <= 6; i++) {
            attractionRepository.save(new Attraction("Attraction " + i, "Description " + i, 10.0, List.of("photo" + i), i * 10));
        }

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mostVisitedAttractions.length()").value(5));
    }

    @Test
    public void testRetrieveAnalytics_NoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/admin/analytics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRetrieveAnalytics_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    // @Test
    // public void testRetrieveAnalytics_ExpiredToken() throws Exception {
    //     String expiredToken = "Bearer <expired_jwt_token>";

    //     mockMvc.perform(get("/api/admin/analytics")
    //             .header("Authorization", expiredToken))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.error").value("Token expired"));
    // }

    @Test
    public void testRetrieveAnalytics_NonAdminToken() throws Exception {
        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", userJwtToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"));
    }

    @Test
    public void testRetrieveAnalytics_LargeNumberOfClicks() throws Exception {
        attractionRepository.saveAll(List.of(
                new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1"), 1_000_000),
                new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2"), 2_000_000)
        ));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(3_000_000))
                .andExpect(jsonPath("$.mostVisitedAttractions.length()").value(2));
    }

    @Test
    public void testRetrieveAnalytics_SameTrafficCount() throws Exception {
        attractionRepository.saveAll(List.of(
                new Attraction("Attraction A", "Description A", 10.0, List.of("photoA"), 500),
                new Attraction("Attraction B", "Description B", 15.0, List.of("photoB"), 500)
        ));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mostVisitedAttractions.length()").value(2))
                .andExpect(jsonPath("$.mostVisitedAttractions[0]").value("Attraction A"));
    }

    @Test
    public void testRetrieveAnalytics_VeryLargeAttractionNames() throws Exception {
        String longName = "A".repeat(255);
        attractionRepository.save(new Attraction(longName, "Description", 10.0, List.of("photo"), 100));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mostVisitedAttractions[0]").value(longName));
    }

    @Test
    public void testRetrieveAnalytics_ImmediatelyAfterAddingAttractions() throws Exception {
        Attraction newAttraction = new Attraction("New Attraction", "Description", 10.0, List.of("photo"), 300);
        attractionRepository.save(newAttraction);

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(300))
                .andExpect(jsonPath("$.mostVisitedAttractions[0]").value("New Attraction"));
    }

    @Test
    public void testRetrieveAnalytics_AfterDeletingAllAttractions() throws Exception {
        attractionRepository.deleteAll();

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(0))
                .andExpect(jsonPath("$.mostVisitedAttractions").isEmpty());
    }

    @Test
    public void testRetrieveAnalytics_AfterResettingTrafficCount() throws Exception {
        List<Attraction> attractions = attractionRepository.findAll();
        attractions.forEach(attraction -> attraction.setTrafficCount(0));
        attractionRepository.saveAll(attractions);

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(0))
                .andExpect(jsonPath("$.mostVisitedAttractions").isEmpty());
    }

    @Test
    public void testRetrieveAnalytics_AfterAddingZeroTrafficAttractions() throws Exception {
        attractionRepository.saveAll(List.of(
                new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1"), 0),
                new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2"), 0)
        ));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(0))
                .andExpect(jsonPath("$.mostVisitedAttractions").isEmpty());
    }

    @Test
    public void testRetrieveAnalytics_WithSpecialCharacterNames() throws Exception {
        attractionRepository.save(new Attraction("@#$%^&*()_+{}:<>?", "Special Name", 10.0, List.of("photo"), 200));

        mockMvc.perform(get("/api/admin/analytics")
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mostVisitedAttractions[0]").value("@#$%^&*()_+{}:<>?"));
    }

}
