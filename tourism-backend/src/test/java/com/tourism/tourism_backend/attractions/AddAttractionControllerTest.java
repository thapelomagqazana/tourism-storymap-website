package com.tourism.tourism_backend.attractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddAttractionControllerTest {

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
     * TC_POS_01: Add a new attraction with valid data.
     */
    @Test
    public void testAddAttraction_ValidData() throws Exception {
        String requestBody = """
            {
                "name": "Eiffel Tower",
                "description": "Famous tower in Paris",
                "entranceFee": 25.0,
                "photos": ["url1", "url2"]
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_POS_02: Add a new attraction with no photos.
     */
    @Test
    public void testAddAttraction_NoPhotos() throws Exception {
        String requestBody = """
            {
                "name": "Colosseum",
                "description": "Ancient Roman amphitheater",
                "entranceFee": 15.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_POS_03: Add a new attraction with only one photo.
     */
    @Test
    public void testAddAttraction_OnePhoto() throws Exception {
        String requestBody = """
            {
                "name": "Statue of Liberty",
                "description": "Iconic statue in New York",
                "entranceFee": 20.0,
                "photos": ["url1"]
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_POS_04: Add a new attraction with maximum allowed name length (255 characters).
     */
    @Test
    public void testAddAttraction_MaxNameLength() throws Exception {
        String requestBody = """
            {
                "name": "%s",
                "description": "Description",
                "entranceFee": 30.0,
                "photos": []
            }
        """.formatted("A".repeat(255));

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_POS_05: Add a new attraction with maximum allowed description length (1000 characters).
     */
    @Test
    public void testAddAttraction_MaxDescriptionLength() throws Exception {
        String requestBody = """
            {
                "name": "Great Wall of China",
                "description": "%s",
                "entranceFee": 10.0,
                "photos": []
            }
        """.formatted("D".repeat(1000));

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_NEG_01: Add a new attraction with missing name.
     */
    @Test
    public void testAddAttraction_MissingName() throws Exception {
        String requestBody = """
                {
                    "description": "A landmark",
                    "entranceFee": 10.0,
                    "photos": []
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    /**
     * TC_NEG_02: Add a new attraction with missing description.
     */
    @Test
    public void testAddAttraction_MissingDescription() throws Exception {
        String requestBody = """
                {
                    "name": "Landmark",
                    "entranceFee": 10.0,
                    "photos": []
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("Description is required"));
    }

    /**
     * TC_NEG_03: Add a new attraction with missing entrance fee.
     */
    @Test
    public void testAddAttraction_MissingEntranceFee() throws Exception {
        String requestBody = """
                {
                    "name": "Landmark",
                    "description": "A landmark",
                    "photos": []
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.entranceFee").value("Entrance fee is required"));
    }

    /**
     * TC_NEG_04: Add a new attraction with invalid entrance fee (negative).
     */
    @Test
    public void testAddAttraction_InvalidEntranceFee() throws Exception {
        String requestBody = """
                {
                    "name": "Landmark",
                    "description": "A landmark",
                    "entranceFee": -10.0,
                    "photos": []
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.entranceFee").value("Entrance fee must be a positive number"));
    }

    /**
     * TC_NEG_05: Add a new attraction with invalid data types.
     */
    @Test
    public void testAddAttraction_InvalidDataTypes() throws Exception {
        String requestBody = """
                {
                    "name": 123,
                    "description": true,
                    "entranceFee": "free",
                    "photos": "url"
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    /**
     * TC_NEG_06: Add a new attraction as a non-admin user.
     */
    @Test
    public void testAddAttraction_NonAdminUser() throws Exception {
        String requestBody = """
                {
                    "name": "Restricted Attraction",
                    "description": "Restricted",
                    "entranceFee": 20.0,
                    "photos": []
                }
                """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"));
    }

    /**
     * TC_NEG_07: Add a new attraction with an invalid JSON body.
     */
    @Test
    public void testAddAttraction_InvalidJsonBody() throws Exception {
        String requestBody = """
                {
                    "name": "Landmark",
                    "description": "A landmark",
                    "entranceFee": 10.0
                """; // Missing closing bracket

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request"));
    }

    /**
     * TC_EDGE_01: Add a new attraction with empty photos array.
     */
    @Test
    public void testAddAttraction_EmptyPhotosArray() throws Exception {
        String requestBody = """
            {
                "name": "Taj Mahal",
                "description": "Famous monument in India",
                "entranceFee": 10.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_EDGE_02: Add a new attraction with entrance_fee = 0.
     */
    @Test
    public void testAddAttraction_ZeroEntranceFee() throws Exception {
        String requestBody = """
            {
                "name": "Free Attraction",
                "description": "No entrance fee required",
                "entranceFee": 0.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_EDGE_03: Add a new attraction with a large number of photos (100 URLs).
     */
    @Test
    public void testAddAttraction_LargeNumberOfPhotos() throws Exception {
        String photos = "[%s]".formatted(String.join(", ", 
            java.util.stream.IntStream.rangeClosed(1, 100)
                .mapToObj(i -> "\"url" + i + "\"")
                .toArray(String[]::new)
        ));

        String requestBody = """
            {
                "name": "Photo Rich Attraction",
                "description": "Many photos",
                "entranceFee": 30.0,
                "photos": %s
            }
        """.formatted(photos);

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_EDGE_04: Add a new attraction with very small entrance_fee (0.01).
     */
    @Test
    public void testAddAttraction_MinimalEntranceFee() throws Exception {
        String requestBody = """
            {
                "name": "Minimal Fee Attraction",
                "description": "Tiny entrance fee",
                "entranceFee": 0.01,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_EDGE_05: Add a new attraction with special characters in the name.
     */
    @Test
    public void testAddAttraction_SpecialCharactersInName() throws Exception {
        String requestBody = """
            {
                "name": "@#$%^&*()_+{}:\\":<>?",
                "description": "Special characters in name",
                "entranceFee": 10.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_CORNER_01: Add a new attraction with whitespace-only name.
     */
    @Test
    public void testAddAttraction_WhitespaceOnlyName() throws Exception {
        String requestBody = """
            {
                "name": " ",
                "description": "Whitespace name",
                "entranceFee": 10.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Name is required"));
    }

    /**
     * TC_CORNER_02: Add a new attraction with name containing leading/trailing spaces.
     */
    @Test
    public void testAddAttraction_LeadingTrailingSpacesInName() throws Exception {
        String requestBody = """
            {
                "name": "   Attraction Name   ",
                "description": "Leading and trailing spaces",
                "entranceFee": 10.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_CORNER_03: Add a new attraction with description containing leading/trailing spaces.
     */
    @Test
    public void testAddAttraction_LeadingTrailingSpacesInDescription() throws Exception {
        String requestBody = """
            {
                "name": "Attraction",
                "description": "   Description with spaces   ",
                "entranceFee": 10.0,
                "photos": []
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    /**
     * TC_CORNER_04: Add a new attraction with photos containing duplicate URLs.
     */
    @Test
    public void testAddAttraction_DuplicatePhotoURLs() throws Exception {
        String requestBody = """
            {
                "name": "Duplicate Photos",
                "description": "Photos have duplicate URLs",
                "entranceFee": 10.0,
                "photos": ["url1", "url1", "url2"]
            }
        """;

        mockMvc.perform(post("/api/attractions")
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Attraction added successfully"));
    }

    // /**
    //  * TC_CORNER_05: Add a new attraction with photos containing invalid URLs.
    //  */
    // @Test
    // public void testAddAttraction_InvalidPhotoURLs() throws Exception {
    //     String requestBody = """
    //         {
    //             "name": "Invalid Photo URLs",
    //             "description": "Photos contain invalid URLs",
    //             "entrance_fee": 10.0,
    //             "photos": ["not-a-url", "url2"]
    //         }
    //     """;

    //     mockMvc.perform(post("/api/attractions")
    //             .header("Authorization", adminJwtToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.error").value("Invalid photo URL format"));
    // }
}
