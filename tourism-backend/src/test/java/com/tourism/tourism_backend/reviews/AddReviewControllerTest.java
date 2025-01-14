package com.tourism.tourism_backend.reviews;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String userJwtToken;
    private Long attractionId1, attractionId2, attractionId3, attractionId4;

    @BeforeEach
    public void setup() throws Exception {
        // Add sample attractions
        Attraction attraction1 = attractionRepository.save(new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1")));
        Attraction attraction2 = attractionRepository.save(new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2")));
        Attraction attraction3 = attractionRepository.save(new Attraction("Attraction 3", "Description 3", 20.0, List.of("photo3")));
        Attraction attraction4 = attractionRepository.save(new Attraction("Attraction 4", "Description 4", 25.0, List.of("photo4")));

        attractionId1 = attraction1.getId();
        attractionId2 = attraction2.getId();
        attractionId3 = attraction3.getId();
        attractionId4 = attraction4.getId();

        // Create a user and obtain their token
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
    public void testAddReview_ValidData() throws Exception {
        String requestBody = """
            {
                "rating": 5,
                "comment": "Great experience!"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    @Test
    public void testAddReview_LowestValidRating() throws Exception {
        String requestBody = """
            {
                "rating": 1,
                "comment": "Could be better."
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId2)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    @Test
    public void testAddReview_HighestValidRating() throws Exception {
        String requestBody = """
            {
                "rating": 5,
                "comment": "Amazing attraction!"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId3)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    @Test
    public void testAddReview_LongComment() throws Exception {
        String longComment = "A".repeat(1000); // Generate a 1000-character long comment
        String requestBody = """
            {
                "rating": 4,
                "comment": "%s"
            }
        """.formatted(longComment);

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    @Test
    public void testAddReview_NoPreviousReviews() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "First review!"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId4)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    @Test
    public void testAddReview_EmptyComment() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": ""
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.reviewId").isNotEmpty());
    }

    // @Test
    // public void testAddReview_SpecialCharactersInComment() throws Exception {
    //     String requestBody = """
    //         {
    //             "rating": 4,
    //             "comment": "@#$%^&*()_+{}:<>?"
    //         }
    //     """;

    //     mockMvc.perform(post("/api/reviews/attraction/" + attractionId2)
    //             .header("Authorization", userJwtToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.message").value("Review added successfully"))
    //             .andExpect(jsonPath("$.reviewId").isNotEmpty());
    // }

    @Test
    public void testAddReview_InvalidRating() throws Exception {
        String requestBody = """
            {
                "rating": 6,
                "comment": "Invalid rating"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId3)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.rating").value("Rating must be at most 5"));
    }

    @Test
    public void testAddReview_MissingRating() throws Exception {
        String requestBody = """
            {
                "comment": "Missing rating"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.rating").value("Rating is required"));
    }

    @Test
    public void testAddReview_MissingComment() throws Exception {
        String requestBody = """
            {
                "rating": 3
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.comment").value("Comment is required"));
    }

    @Test
    public void testAddReview_NonExistentAttraction() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "Great!"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/9999")
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9999"));
    }

    @Test
    public void testAddReview_NoAuthorizationHeader() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "Great!"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAddReview_InvalidToken() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "Invalid token"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", "Bearer invalidToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }


    @Test
    public void testAddReview_NonLoggedInUser() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "Access denied"
            }
        """;

        mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                .header("Authorization", "Bearer nonExistentUserToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    @Test
    public void testAddReview_MultipleTimesForSameAttraction() throws Exception {
        String requestBody = """
            {
                "rating": 4,
                "comment": "Consistent quality!"
            }
        """;

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
                    .header("Authorization", userJwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Review added successfully"))
                    .andExpect(jsonPath("$.reviewId").isNotEmpty());
        }
    }

    // @Test
    // public void testAddReview_FloatingPointRating() throws Exception {
    //     String requestBody = """
    //         {
    //             "rating": 4.5,
    //             "comment": "Great!"
    //         }
    //     """;

    //     mockMvc.perform(post("/api/reviews/attraction/" + attractionId1)
    //             .header("Authorization", userJwtToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.error").value("Rating must be an integer"));
    // }


}