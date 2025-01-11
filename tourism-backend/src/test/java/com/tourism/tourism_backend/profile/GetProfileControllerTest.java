package com.tourism.tourism_backend.profile;

import com.tourism.tourism_backend.dto.LoginRequest;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.BlacklistedTokenRepository;
import com.tourism.tourism_backend.repositories.UserRepository;
import com.tourism.tourism_backend.services.AuthService;
import com.tourism.tourism_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GetProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

        @Autowired
    private AuthService authService;

    private String maxNameToken;
    private String maxEmailToken;
    private String shortToken;

    private String validJwtToken1;
    private String validJwtToken2;
    private String validJwtTokenComplexName;
    private String expiredJwtToken;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        // Clear the database before each test
        userRepository.deleteAll();
        blacklistedTokenRepository.deleteAll();

        // Create test users
        AppUser user1 = new AppUser("John Doe", "john.doe@example.com", "password123");
        AppUser user2 = new AppUser("Jane Doe", "jane.doe@example.com", "password123");
        AppUser userComplex = new AppUser("John@#Doe123", "complex.user@example.com", "password123");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(userComplex);

        // Generate valid JWT tokens for the test users
        validJwtToken1 = jwtUtil.generateToken(user1.getEmail(), user1.getRole());
        validJwtToken2 = jwtUtil.generateToken(user2.getEmail(), user2.getRole());
        validJwtTokenComplexName = jwtUtil.generateToken(userComplex.getEmail(), userComplex.getRole());

        // Generate expired JWT token (using a utility method that generates expired tokens)
        expiredJwtToken = jwtUtil.generateExpiredToken(user1.getEmail());

        // // Create a user with a 255-character name and generate a token
        // UserDTO maxNameUser = new UserDTO();
        // maxNameUser.setName("A".repeat(255));
        // maxNameUser.setEmail("max.name@example.com");
        // maxNameUser.setPassword("password123");
        // authService.registerUser(maxNameUser);
        // maxNameToken = authService.authenticateUser(new LoginRequest("max.name@example.com", "password123"));

        // // Create a user with a 255-character email and generate a token
        // UserDTO maxEmailUser = new UserDTO();
        // maxEmailUser.setName("Max Email");
        // maxEmailUser.setEmail("A".repeat(247) + "@example.com"); // Total length = 255
        // maxEmailUser.setPassword("password123");
        // authService.registerUser(maxEmailUser);
        // maxEmailToken = authService.authenticateUser(new LoginRequest(maxEmailUser.getEmail(), "password123"));

        // // Generate a short valid token manually (for testing purposes)
        // shortToken = "short.valid.jwt.token"; // Replace with a generated short valid token if needed
    }

    /**
     * Test Case ID: TC_POS_01
     * Test retrieving profile of a valid logged-in user.
     */
    @Test
    public void testRetrieveProfile_ValidUser() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + validJwtToken1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test Case ID: TC_POS_02
     * Test retrieving profile after login.
     */
    @Test
    public void testRetrieveProfile_AfterLogin() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + validJwtToken2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    /**
     * Test Case ID: TC_POS_03
     * Test retrieving profile with a user having a complex name.
     */
    @Test
    public void testRetrieveProfile_ComplexName() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + validJwtTokenComplexName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John@#Doe123"))
                .andExpect(jsonPath("$.email").value("complex.user@example.com"));
    }

     /**
     * TC_NEG_01: Attempt to retrieve profile without a token
     */
    @Test
    public void testRetrieveProfile_NoToken() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * TC_NEG_02: Attempt to retrieve profile with an invalid token
     */
    @Test
    public void testRetrieveProfile_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer INVALID_TOKEN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token format"));
    }

    /**
     * TC_NEG_03: Attempt to retrieve profile with an expired token
     */
    @Test
    public void testRetrieveProfile_ExpiredToken() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + expiredJwtToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token has expired"));
    }

    /**
     * TC_NEG_04: Attempt to retrieve profile for a deleted user
     */
    // @Test
    // public void testRetrieveProfile_DeletedUser() throws Exception {
    //     // Delete the user from the database
    //     userRepository.deleteAll();

    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + validJwtToken1))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.error").value("User not found with email: john.doe@example.com"));
    // }

    // @Test
    // public void testRetrieveProfile_MaxNameLength() throws Exception {
    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + maxNameToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.name").value("A".repeat(255)));
    // }

    // @Test
    // public void testRetrieveProfile_MaxEmailLength() throws Exception {
    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + maxEmailToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.email").value("A".repeat(247) + "@example.com"));
    // }

    // @Test
    // public void testRetrieveProfile_ShortToken() throws Exception {
    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + shortToken))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.name").exists())
    //             .andExpect(jsonPath("$.email").exists());
    // }

    /**
     * TC_CORNER_01: Retrieve profile for a user with Unicode characters in the name.
     */
    @Test
    public void testRetrieveProfile_UserWithUnicodeName() throws Exception {
        // Create a user with Unicode characters in the name
        AppUser user = new AppUser("Йон До", "unicode.user@example.com", "password123");
        userRepository.save(user);
        jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Perform GET request and verify response
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Йон До"))
                .andExpect(jsonPath("$.email").value("unicode.user@example.com"));
    }

    /**
     * TC_CORNER_02: Retrieve profile for a user with special characters in the email.
     */
    @Test
    public void testRetrieveProfile_UserWithSpecialCharacterEmail() throws Exception {
        // Create a user with special characters in the email
        AppUser user = new AppUser("Special Email", "john+filter@example.com", "password123");
        userRepository.save(user);
        jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Perform GET request and verify response
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Special Email"))
                .andExpect(jsonPath("$.email").value("john+filter@example.com"));
    }

    /**
     * TC_CORNER_03: Retrieve profile for a user with leading/trailing spaces in the name.
     */
    @Test
    public void testRetrieveProfile_UserWithSpacesInName() throws Exception {
        // Create a user with leading/trailing spaces in the name
        AppUser user = new AppUser("  John Doe  ", "john.spaces@example.com", "password123");
        userRepository.save(user);
        jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Perform GET request and verify response
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe")) // Spaces should be trimmed
                .andExpect(jsonPath("$.email").value("john.spaces@example.com"));
    }

    /**
     * TC_CORNER_04: Retrieve profile for a user with mixed-case email.
     */
    @Test
    public void testRetrieveProfile_UserWithMixedCaseEmail() throws Exception {
        // Create a user with mixed-case email
        AppUser user = new AppUser("Mixed Case", "John.Doe@Example1.Com", "password123");
        userRepository.save(user);
        jwtToken = jwtUtil.generateToken(user.getEmail().toLowerCase(), user.getRole());

        // Perform GET request and verify response
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mixed Case"))
                .andExpect(jsonPath("$.email").value("John.Doe@Example1.Com"));
    }
}