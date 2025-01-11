package com.tourism.tourism_backend.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.models.AppUser;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UpdateProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    public void setup() throws Exception {
        userRepository.deleteAll();
        // Generate a unique email for the test user
        String uniqueEmail = "john.doe" + System.currentTimeMillis() + "@example.com";

        // Create a sample user with a hashed password
        String rawPassword = "password123";
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        AppUser user = new AppUser("John Doe", uniqueEmail, hashedPassword, "USER");
        userRepository.save(user);

        // Generate a valid JWT token using the unique email and raw password
        jwtToken = "Bearer " + obtainJwtToken(uniqueEmail, rawPassword);
    }

    /**
     * Helper method to obtain JWT token for a user.
     */
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
     * TC_POS_01: Update profile with valid name and email.
     */
    @Test
    public void testUpdateProfile_ValidNameAndEmail() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_POS_02: Update profile with valid name, email, and password.
     */
    @Test
    public void testUpdateProfile_ValidNameEmailPassword() throws Exception {
        UserDTO updateRequest = new UserDTO("Jane Doe", "jane.doe@example.com", "newpassword123");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    /**
     * TC_POS_03: Update profile with only the name.
     */
    @Test
    public void testUpdateProfile_OnlyName() throws Exception {
        UserDTO updateRequest = new UserDTO();
        updateRequest.setName("Updated User");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
                // .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_POS_04: Update profile with only the email.
     */
    @Test
    public void testUpdateProfile_OnlyEmail() throws Exception {
        UserDTO updateRequest = new UserDTO();
        updateRequest.setEmail("updated.email@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"));
    }

    /**
     * TC_POS_05: Update profile with a complex name.
     */
    @Test
    public void testUpdateProfile_ComplexName() throws Exception {
        UserDTO updateRequest = new UserDTO("John@#Doe123", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John@#Doe123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_NEG_01: Update profile with invalid email format.
     */
    @Test
    public void testUpdateProfile_InvalidEmailFormat() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "invalid-email");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    /**
     * TC_NEG_02: Update profile with an email already in use.
     */
    @Test
    public void testUpdateProfile_EmailAlreadyInUse() throws Exception {
        // Create another user with a different email
        AppUser anotherUser = new AppUser("Existing User", "existing.user@example.com", "password123");
        userRepository.save(anotherUser);

        UserDTO updateRequest = new UserDTO("John Doe", "existing.user@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    /**
     * TC_NEG_03: Update profile without required fields.
     */
    @Test
    public void testUpdateProfile_EmptyRequest() throws Exception {
        UserDTO updateRequest = new UserDTO(); // Empty request

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("At least one field is required for update"));
    }

    /**
     * TC_NEG_04: Update profile with null fields.
     */
    @Test
    public void testUpdateProfile_NullFields() throws Exception {
        String requestBody = "{\"name\": null, \"email\": null, \"password\": null}";

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("At least one field is required for update"));
    }

    /**
     * TC_NEG_05: Update profile with invalid password (too short).
     */
    @Test
    public void testUpdateProfile_ShortPassword() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "john.doe@example.com", "123");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password must be at least 8 characters long"));
    }

    /**
     * TC_NEG_06: Attempt to update profile without authentication token.
     */
    @Test
    public void testUpdateProfile_NoToken() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_07: Attempt to update profile with an invalid token.
     */
    @Test
    public void testUpdateProfile_InvalidToken() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_CORNER_01: Update profile with Unicode characters in the name.
     */
    @Test
    public void testUpdateProfile_UnicodeName() throws Exception {
        UserDTO updateRequest = new UserDTO("Йон До", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Йон До"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_CORNER_02: Update profile with special characters in the name.
     */
    @Test
    public void testUpdateProfile_SpecialCharactersInName() throws Exception {
        UserDTO updateRequest = new UserDTO("John@#Doe", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John@#Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_CORNER_03: Update profile with leading/trailing spaces in the name.
     */
    @Test
    public void testUpdateProfile_LeadingTrailingSpacesInName() throws Exception {
        UserDTO updateRequest = new UserDTO(" John Doe ", "john.doe@example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_CORNER_04: Update profile with email containing subdomains.
     */
    @Test
    public void testUpdateProfile_EmailWithSubdomains() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "user@sub.domain.example.com");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("user@sub.domain.example.com"));
    }

    /**
     * TC_CORNER_05: Update profile with mixed-case email.
     */
    @Test
    public void testUpdateProfile_MixedCaseEmail() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "JoHn.DoE@ExAmPlE.CoM");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("JoHn.DoE@ExAmPlE.CoM"));
    }

    /**
     * TC_CORNER_06: Update profile with password containing special characters.
     */
    @Test
    public void testUpdateProfile_PasswordWithSpecialCharacters() throws Exception {
        UserDTO updateRequest = new UserDTO("John Doe", "john.doe@example.com", "P@ssw0rd!#123");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * TC_CORNER_07: Update profile with mixed whitespace in fields.
     */
    @Test
    public void testUpdateProfile_MixedWhitespaceInFields() throws Exception {
        UserDTO updateRequest = new UserDTO(" John Doe ", " john.doe@example.com ", " password123 ");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }
}
