package com.tourism.tourism_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.dto.LoginRequest;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.BlacklistedTokenRepository;
import com.tourism.tourism_backend.repositories.UserRepository;
import com.tourism.tourism_backend.services.TokenBlacklistService;
import com.tourism.tourism_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private String validToken;
    private String nearExpiryToken;

    @BeforeEach
    public void setup() {
        // Clear the database before each test
        userRepository.deleteAll();
        blacklistedTokenRepository.deleteAll();

        // Create and save a test user
        AppUser user = new AppUser("John Doe", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Generate a valid JWT token for the user
        validToken = jwtUtil.generateToken("john.doe@example.com");

        // Generate a near-expiry token (token with short TTL for testing)
        nearExpiryToken = jwtUtil.generateTokenWithShortExpiry("john.doe@example.com");
    }

    /**
     * Test Case ID: TC_POS_01
     * Test for logging out with a valid token.
     */
    @Test
    public void testLogoutUser_ValidToken() throws Exception {
        // Generate a valid token for the test user
        // String validToken1 = jwtUtil.generateToken("john.doe@example.com");

        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));
    }

    /**
     * Test Case ID: TC_POS_02
     * Test for logging out immediately after login with a newly generated token.
     */
    @Test
    public void testLogoutUser_AfterLogin() throws Exception {
        // Simulate a login request to get a newly generated token
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        // Generate a valid token and log in
        String token = jwtUtil.generateToken("john.doe@example.com");

        // Perform logout request
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));
    }

    /**
     * Test Case ID: TC_POS_03
     * Test for logging out with a token close to expiry.
     */
    @Test
    public void testLogoutUser_NearExpiryToken() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer " + nearExpiryToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));
    }

    @Test
    public void testLogoutUser_MissingToken() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
                // .andExpect(jsonPath("$.message").value("Missing or invalid Authorization header"));
    }

    @Test
    public void testLogoutUser_InvalidTokenFormat() throws Exception {
        // Authorization header with invalid token format
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    @Test
    public void testLogoutUser_MalformedToken() throws Exception {
        // Authorization header with malformed token
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer malformed.token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    @Test
    public void testLogoutUser_BlacklistedToken() throws Exception {
        // Generate a valid token
        String token = jwtUtil.generateToken("john.doe@example.com");

        // Blacklist the token
        tokenBlacklistService.blacklistToken(token);

        // Attempt to logout with the blacklisted token
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token is blacklisted"));
    }

    // @Test
    // public void testLogoutUser_ExpiredToken() throws Exception {
    //     // Generate a token with a short expiry (already expired)
    //     String expiredToken = jwtUtil.generateTokenWithShortExpiry("john.doe@example.com");

    //     // Simulate token expiry by waiting for its expiration (optional if expiry is already immediate)
    //     Thread.sleep(61000); // 61 seconds to ensure expiry

    //     // Attempt to logout with the expired token
    //     mockMvc.perform(post("/api/users/logout")
    //             .header("Authorization", "Bearer " + expiredToken))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.error").value("Token is blacklisted"));
    // }

    @Test
    public void testLogoutUser_WithoutBearerPrefix() throws Exception {
        // Generate a valid token
        String token = jwtUtil.generateToken("john.doe@example.com");

        // Attempt to logout without "Bearer" prefix
        mockMvc.perform(post("/api/users/logout")
                .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    // @Test
    // public void testLogoutUser_NullAuthorizationHeader() throws Exception {
    //     // Null Authorization header
    //     mockMvc.perform(post("/api/users/logout")
    //             .header("Authorization", (String) null))
    //             .andExpect(status().isUnauthorized());
    // }
}
