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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UpdateAttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken;

    private Long attractionId;

    @BeforeEach
    public void setup() throws Exception {
        userRepository.deleteAll();
        attractionRepository.deleteAll();

        // Create admin user
        AppUser adminUser = new AppUser("Admin", "admin@example.com",
                new BCryptPasswordEncoder().encode("admin123"), "ADMIN");
        userRepository.save(adminUser);
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");

        // Create a sample attraction
        Attraction attraction = new Attraction("Old Name", "Old description", 20.0, null);
        attractionRepository.save(attraction);

        attractionId = attraction.getId(); // Get the ID of the saved user
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
     * TC_POS_01: Update attraction with valid data.
     */
    @Test
    public void testUpdateAttraction_ValidData() throws Exception {
        String requestBody = """
            {
                "name": "New Name",
                "description": "Updated description",
                "entranceFee": 50.0,
                "photos": []
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.shortDescription").value("Updated description"))
                .andExpect(jsonPath("$.entranceFee").value(50.0));
    }

    /**
     * TC_POS_02: Update attraction with only the name field.
     */
    @Test
    public void testUpdateAttraction_OnlyName() throws Exception {
        String requestBody = """
            {
                "name": "Updated Name"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    /**
     * TC_POS_03: Update attraction with only the description field.
     */
    @Test
    public void testUpdateAttraction_OnlyDescription() throws Exception {
        String requestBody = """
            {
                "description": "New description"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Old Name"))
                .andExpect(jsonPath("$.shortDescription").value("New description"));
    }

    /**
     * TC_POS_04: Update attraction with an entrance fee of zero.
     */
    @Test
    public void testUpdateAttraction_ZeroEntranceFee() throws Exception {
        String requestBody = """
            {
                "entranceFee": 0.0
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entranceFee").value(0.0));
    }

    /**
     * TC_POS_05: Update attraction with only one photo.
     */
    @Test
    public void testUpdateAttraction_OnePhoto() throws Exception {
        String requestBody = """
            {
                "photos": ["url1"]
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos[0]").value("url1"));
    }

    /**
     * TC_POS_06: Update attraction with multiple fields.
     */
    @Test
    public void testUpdateAttraction_MultipleFields() throws Exception {
        String requestBody = """
            {
                "name": "Updated",
                "description": "Updated",
                "entranceFee": 10.0,
                "photos": ["url1", "url2"]
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.shortDescription").value("Updated"))
                .andExpect(jsonPath("$.entranceFee").value(10.0))
                .andExpect(jsonPath("$.photos[0]").value("url1"))
                .andExpect(jsonPath("$.photos[1]").value("url2"));
    }

        /**
     * TC_NEG_01: Update attraction with invalid ID (non-existent ID).
     */
    @Test
    public void testUpdateAttraction_InvalidId() throws Exception {
        String requestBody = """
            {
                "name": "Invalid ID Test"
            }
        """;

        mockMvc.perform(put("/api/attractions/999") // Non-existent ID
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 999"));
    }

    /**
     * TC_NEG_02: Update attraction without authentication.
     */
    @Test
    public void testUpdateAttraction_NoAuth() throws Exception {
        String requestBody = """
            {
                "name": "No Auth"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_03: Update attraction as a non-admin user.
     */
    @Test
    public void testUpdateAttraction_NonAdminUser() throws Exception {
        // Create a non-admin user and obtain token
        AppUser regularUser = new AppUser("User", "user@example.com",
                new BCryptPasswordEncoder().encode("user123"), "USER");
        userRepository.save(regularUser);
        String userJwtToken = "Bearer " + obtainJwtToken("user@example.com", "user123");

        String requestBody = """
            {
                "name": "Non-Admin Update"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"));
    }

    /**
     * TC_NEG_04: Update attraction with invalid JSON format.
     */
    @Test
    public void testUpdateAttraction_InvalidJsonFormat() throws Exception {
        String requestBody = """
            {
                "name": "Incomplete JSON", "description": "Missing bracket"
        """; // Missing closing bracket

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    /**
     * TC_NEG_05: Update attraction with invalid data types.
     */
    @Test
    public void testUpdateAttraction_InvalidDataTypes() throws Exception {
        String requestBody = """
            {
                "name": 123,
                "description": true,
                "entranceFee": "free",
                "photos": "url"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    /**
     * TC_NEG_06: Update attraction with missing required fields.
     */
    @Test
    public void testUpdateAttraction_MissingRequiredFields() throws Exception {
        String requestBody = """
            { }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one field is required for update"));
    }

    /**
     * TC_EDGE_01: Update attraction with a very long name (255 characters).
     */
    @Test
    public void testUpdateAttraction_VeryLongName() throws Exception {
        String requestBody = """
            {
                "name": "%s"
            }
        """.formatted("A".repeat(255));

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A".repeat(255)));
    }

    /**
     * TC_EDGE_02: Update attraction with a very long description (1000 characters).
     */
    @Test
    public void testUpdateAttraction_VeryLongDescription() throws Exception {
        String requestBody = """
            {
                "description": "%s"
            }
        """.formatted("D".repeat(1000));

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortDescription").value("D".repeat(1000)));
    }

    /**
     * TC_EDGE_03: Update attraction with maximum number of photos (100 URLs).
     */
    @Test
    public void testUpdateAttraction_MaxPhotos() throws Exception {
        List<String> photos = IntStream.rangeClosed(1, 100)
                                    .mapToObj(i -> "url" + i)
                                    .collect(Collectors.toList());

        String requestBody = new ObjectMapper().writeValueAsString(Map.of("photos", photos));

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos.length()").value(100))
                .andExpect(jsonPath("$.photos[0]").value("url1"))
                .andExpect(jsonPath("$.photos[99]").value("url100"));
    }

    /**
     * TC_EDGE_04: Update attraction with special characters in name and description.
     */
    @Test
    public void testUpdateAttraction_SpecialCharacters() throws Exception {
        String requestBody = """
            {
                "name": "@#$%^&*()_+{}",
                "description": "<>'\\\"&{}"
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("@#$%^&*()_+{}"))
                .andExpect(jsonPath("$.shortDescription").value("<>'\"&{}"));
    }

    /**
     * TC_EDGE_05: Update attraction with a very small entrance fee (0.01).
     */
    @Test
    public void testUpdateAttraction_SmallEntranceFee() throws Exception {
        String requestBody = """
            {
                "entranceFee": 0.01
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entranceFee").value(0.01));
    }

    /**
     * TC_EDGE_06: Update attraction with null photos.
     */
    @Test
    public void testUpdateAttraction_NullPhotos() throws Exception {
        String requestBody = """
            {
                "photos": null
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one field is required for update"));
    }

    /**
     * TC_CORNER_01: Update attraction with an ID of zero.
     */
    @Test
    public void testUpdateAttraction_ZeroId() throws Exception {
        String requestBody = """
            {
                "name": "Zero ID Test"
            }
        """;

        mockMvc.perform(put("/api/attractions/0")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 0"));
    }

    /**
     * TC_CORNER_02: Update attraction with a negative ID.
     */
    @Test
    public void testUpdateAttraction_NegativeId() throws Exception {
        String requestBody = """
            {
                "name": "Negative ID Test"
            }
        """;

        mockMvc.perform(put("/api/attractions/-1")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: -1"));
    }

    /**
     * TC_CORNER_03: Update attraction with a large ID (e.g., 2^63 - 1).
     */
    @Test
    public void testUpdateAttraction_LargeId() throws Exception {
        String requestBody = """
            {
                "name": "Large ID Test"
            }
        """;

        mockMvc.perform(put("/api/attractions/9223372036854775807")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Attraction not found with ID: 9223372036854775807"));
    }

    /**
     * TC_CORNER_04: Update attraction with null fields.
     */
    @Test
    public void testUpdateAttraction_NullFields() throws Exception {
        String requestBody = """
            {
                "name": null,
                "description": null,
                "entranceFee": null,
                "photos": null
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one field is required for update"));
    }

    /**
     * TC_CORNER_05: Update attraction with empty string fields.
     */
    @Test
    public void testUpdateAttraction_EmptyStringFields() throws Exception {
        String requestBody = """
            {
                "name": "",
                "description": "",
                "entranceFee": 0,
                "photos": []
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    /**
     * TC_CORNER_06: Update attraction with whitespace-only fields.
     */
    @Test
    public void testUpdateAttraction_WhitespaceFields() throws Exception {
        String requestBody = """
            {
                "name": " ",
                "description": " ",
                "entranceFee": 0,
                "photos": []
            }
        """;

        mockMvc.perform(put("/api/attractions/" + attractionId)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}
