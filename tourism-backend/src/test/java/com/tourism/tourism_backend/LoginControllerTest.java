package com.tourism.tourism_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.dto.LoginRequest;
import com.tourism.tourism_backend.models.User;
import com.tourism.tourism_backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        // Prepopulate users for test cases
        userRepository.save(new User("John Doe", "john.doe@example.com", passwordEncoder.encode("password123")));
        userRepository.save(new User("Secure User", "secure.user@example.com", passwordEncoder.encode("P@ssw0rd#123")));
    }

    /**
     * Test Case ID: TC_POS_01
     * Log in with valid email and password.
     */
    @Test
    public void testLoginUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * Test Case ID: TC_POS_02
     * Log in with email containing uppercase letters.
     */
    @Test
    public void testLoginUser_EmailInUppercase() throws Exception {
        LoginRequest loginRequest = new LoginRequest("JOHN.DOE@EXAMPLE.COM", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * Test Case ID: TC_POS_03
     * Log in with valid credentials after trimming leading and trailing spaces.
     */
    @Test
    public void testLoginUser_TrimmedCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest(" john.doe@example.com ", " password123 ");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * Test Case ID: TC_POS_04
     * Log in with a complex password.
     */
    @Test
    public void testLoginUser_ComplexPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("secure.user@example.com", "P@ssw0rd#123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * Test Case ID: TC_POS_05
     * Log in immediately after registration.
     */
    @Test
    public void testLoginUser_AfterRegistration() throws Exception {
        // Register a new user
        User newUser = new User("New User", "new.user@example.com", passwordEncoder.encode("password123"));
        userRepository.save(newUser);

        // Attempt login with the new user's credentials
        LoginRequest loginRequest = new LoginRequest("new.user@example.com", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_NEG_01: Log in with a non-existent email.
     */
    @Test
    public void testLoginUser_NonExistentEmail() throws Exception {
        LoginRequest request = new LoginRequest("non.existent@example.com", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    /**
     * TC_NEG_02: Log in with an incorrect password.
     */
    @Test
    public void testLoginUser_IncorrectPassword() throws Exception {
        LoginRequest request = new LoginRequest("john.doe@example.com", "wrongpassword");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    /**
     * TC_NEG_03: Log in with a missing email.
     */
    @Test
    public void testLoginUser_MissingEmail() throws Exception {
        String requestJson = "{ \"password\": \"password123\" }";

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email cannot be null or empty"));
    }

    /**
     * TC_NEG_04: Log in with a missing password.
     */
    @Test
    public void testLoginUser_MissingPassword() throws Exception {
        String requestJson = "{ \"email\": \"john.doe@example.com\" }";

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password cannot be null or empty"));
    }

    /**
     * TC_NEG_05: Log in with empty fields.
     */
    @Test
    public void testLoginUser_EmptyFields() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email cannot be null or empty"));
    }

    /**
     * TC_NEG_06: Log in with an invalid email format.
     */
    @Test
    public void testLoginUser_InvalidEmailFormat() throws Exception {
        LoginRequest request = new LoginRequest("john.doe@", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    /**
     * TC_NEG_07: Log in with null fields.
     */
    @Test
    public void testLoginUser_NullFields() throws Exception {
        String requestJson = "{ \"email\": null, \"password\": null }";

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email cannot be null or empty"));
    }

    /**
     * TC_EDGE_01: Log in with maximum allowed email length.
     */
    @Test
    public void testLoginUser_MaxEmailLength() throws Exception {
        String maxEmail = "a".repeat(243) + "@example.com"; // Total length = 255
        userRepository.save(new User("Max Email", maxEmail, passwordEncoder.encode("password123")));

        LoginRequest request = new LoginRequest(maxEmail, "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_EDGE_02: Log in with maximum allowed password length.
     */
    @Test
    public void testLoginUser_MaxPasswordLength() throws Exception {
        String maxPassword = "p".repeat(255);
        userRepository.save(new User("Max Password", "max.password@example.com", passwordEncoder.encode(maxPassword)));

        LoginRequest request = new LoginRequest("max.password@example.com", maxPassword);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_EDGE_03: Log in with minimum allowed password length (8 characters).
     */
    @Test
    public void testLoginUser_MinPasswordLength() throws Exception {
        userRepository.save(new User("Min Password", "min.password@example.com", passwordEncoder.encode("pass1234")));

        LoginRequest request = new LoginRequest("min.password@example.com", "pass1234");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_EDGE_04: Log in with minimum allowed email length.
     */
    @Test
    public void testLoginUser_MinEmailLength() throws Exception {
        userRepository.save(new User("Min Email", "a@b.co", passwordEncoder.encode("password123")));

        LoginRequest request = new LoginRequest("a@b.co", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_EDGE_05: Log in with email and password containing spaces.
     */
    @Test
    public void testLoginUser_EmailAndPasswordWithSpaces() throws Exception {
        LoginRequest request = new LoginRequest(" john.doe@example.com ", " password123 ");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_01: Log in with an email containing subdomains.
     */
    @Test
    public void testLoginUser_EmailWithSubdomains() throws Exception {
        userRepository.save(new User("Subdomain User", "user@sub.domain.example.com", passwordEncoder.encode("password123")));

        LoginRequest request = new LoginRequest("user@sub.domain.example.com", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_02: Log in with email containing special characters.
     */
    @Test
    public void testLoginUser_EmailWithSpecialCharacters() throws Exception {
        userRepository.save(new User("Special User", "john+filter@example.com", passwordEncoder.encode("password123")));

        LoginRequest request = new LoginRequest("john+filter@example.com", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_03: Log in with email containing leading/trailing spaces.
     */
    @Test
    public void testLoginUser_EmailWithLeadingTrailingSpaces() throws Exception {
        LoginRequest request = new LoginRequest(" john.doe@example.com ", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_04: Log in with password containing special characters.
     */
    @Test
    public void testLoginUser_PasswordWithSpecialCharacters() throws Exception {
        LoginRequest request = new LoginRequest("secure.user@example.com", "P@ssw0rd#123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_05: Log in with Unicode characters in the email.
     */
    @Test
    public void testLoginUser_EmailWithUnicodeCharacters() throws Exception {
        String unicodeEmail = "user\u0040example.com"; // Unicode '@' character
        userRepository.save(new User("Unicode User", unicodeEmail, passwordEncoder.encode("password123")));

        LoginRequest request = new LoginRequest(unicodeEmail, "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_06: Log in with a non-ASCII password.
     */
    @Test
    public void testLoginUser_NonAsciiPassword() throws Exception {
        userRepository.save(new User("Non-ASCII User", "non.ascii@example.com", passwordEncoder.encode("pässwörd123")));

        LoginRequest request = new LoginRequest("non.ascii@example.com", "pässwörd123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    /**
     * TC_CORNER_07: Log in with a password containing escape sequences.
     */
    @Test
    public void testLoginUser_PasswordWithEscapeSequences() throws Exception {
        userRepository.save(new User("Escape User", "escape.user@example.com", passwordEncoder.encode("pass\\123")));

        LoginRequest request = new LoginRequest("escape.user@example.com", "pass\\123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
