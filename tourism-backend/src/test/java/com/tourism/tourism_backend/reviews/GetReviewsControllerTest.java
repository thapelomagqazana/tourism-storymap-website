package com.tourism.tourism_backend.reviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Review;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.ReviewRepository;
import com.tourism.tourism_backend.repositories.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetReviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private String userJwtToken;
    private String adminJwtToken;
    private Long attractionIdWithReviews, attractionIdWithoutReviews;

    @BeforeEach
    public void setup() throws Exception {
        // Create sample attractions
        Attraction attractionWithReviews = attractionRepository.save(new Attraction("Attraction With Reviews", "Description", 10.0, List.of("photo1")));
        Attraction attractionWithoutReviews = attractionRepository.save(new Attraction("Attraction Without Reviews", "Description", 10.0, List.of("photo2")));
        
        attractionIdWithReviews = attractionWithReviews.getId();
        attractionIdWithoutReviews = attractionWithoutReviews.getId();

        // Create a user and an admin, and obtain their tokens
        userRepository.save(new AppUser("User", "user@example.com", new BCryptPasswordEncoder().encode("user123"), "USER"));
        userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123");

        userRepository.save(new AppUser("Admin", "admin@example.com", new BCryptPasswordEncoder().encode("admin123"), "ADMIN"));
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");

        // Add sample reviews for attractionWithReviews
        AppUser user = userRepository.findByEmail("user@example.com").orElseThrow();
        reviewRepository.save(new Review(attractionWithReviews, user, 5, "Great experience!"));
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
    public void testRetrieveReviews_WithReviews() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithReviews)
                .header("Authorization", userJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].user").value("User"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Great experience!"));
    }

    @Test
    public void testRetrieveReviews_NoReviews() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithoutReviews)
                .header("Authorization", userJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testRetrieveReviews_ValidUserToken() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithReviews)
                .header("Authorization", userJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testRetrieveReviews_ValidAdminToken() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithReviews)
                .header("Authorization", adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testRetrieveReviews_NonExistentAttraction() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/9999")
                .header("Authorization", userJwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9999"));
    }

    @Test
    public void testRetrieveReviews_NoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithReviews))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRetrieveReviews_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/reviews/attraction/" + attractionIdWithReviews)
                .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }
}
