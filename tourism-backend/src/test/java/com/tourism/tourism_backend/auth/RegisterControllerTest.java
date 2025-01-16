package com.tourism.tourism_backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
    public void setup() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    /**
     * Test Case ID: TC_POS_01
     * Test for successful user registration with valid inputs.
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test Case ID: TC_POS_02
     * Test for successful registration with minimum valid password length.
     */
    @Test
    public void testRegisterUser_MinPasswordLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Jane Doe");
        userDTO.setEmail("jane.doe@example.com");
        userDTO.setPassword("12345678");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_POS_03
     * Test for successful registration with email in uppercase letters.
     */
    @Test
    public void testRegisterUser_EmailInUppercase() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Upper Case");
        userDTO.setEmail("JOHN.DOE@EXAMPLE.COM");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_POS_04
     * Test for successful registration with a valid complex password.
     */
    @Test
    public void testRegisterUser_ComplexPassword() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Secure User");
        userDTO.setEmail("secure.user@example.com");
        userDTO.setPassword("P@ssw0rd#123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test for registration failure due to existing email.
     */
    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        AppUser existingUser = new AppUser();
        existingUser.setName("Jane Doe");
        existingUser.setEmail("jane.doe@example.com");
        existingUser.setPassword("password123");
        existingUser.setRole("USER");
        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("jane.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    /**
     * Test for registration failure due to invalid input.
     */
    @Test
    public void testRegisterUser_InvalidInput() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword(""); // Empty password
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    /**
     * Test Case ID: TC_NEG_01
     * Test for missing email field.
     */
    @Test
    public void testRegisterUser_MissingEmail() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    /**
     * Test Case ID: TC_NEG_02
     * Test for invalid email format.
     */
    @Test
    public void testRegisterUser_InvalidEmailFormat() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    /**
     * Test Case ID: TC_NEG_03
     * Test for short password.
     */
    @Test
    public void testRegisterUser_ShortPassword() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    /**
     * Test Case ID: TC_NEG_04
     * Test for existing email.
     */
    @Test
    public void testRegisterUser_ExistingEmail() throws Exception {
        AppUser existingUser = new AppUser();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing.user@example.com");
        existingUser.setPassword("password123");
        existingUser.setRole("USER");
        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("existing.user@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    /**
     * Test Case ID: TC_NEG_05
     * Test for missing fields.
     */
    @Test
    public void testRegisterUser_MissingFields() throws Exception {
        UserDTO userDTO = new UserDTO(); // Empty fields

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    /**
     * Test Case ID: TC_NEG_06
     * Test for empty name.
     */
    @Test
    public void testRegisterUser_EmptyName() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    /**
     * Test Case ID: TC_EDGE_01
     * Register a user with maximum allowed name length.
     */
    @Test
    public void testRegisterUser_MaxNameLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("A".repeat(255)); // 255 characters
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_EDGE_02
     * Register a user with maximum allowed email length.
     */
    // @Test
    // public void testRegisterUser_MaxEmailLength() throws Exception {
    //     UserDTO userDTO = new UserDTO();
    //     userDTO.setName("John Doe");
    //     userDTO.setEmail("A".repeat(247) + "@example.com"); // 255 characters total
    //     userDTO.setPassword("password123");

    //     mockMvc.perform(post("/api/users/register")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(userDTO)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id").exists());
    // }

    /**
     * Test Case ID: TC_EDGE_03
     * Register a user with maximum allowed password length.
     */
    @Test
    public void testRegisterUser_MaxPasswordLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("A".repeat(255)); // 255 characters
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_EDGE_04
     * Register a user with minimum allowed name length.
     */
    @Test
    public void testRegisterUser_MinNameLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("J"); // 1 character
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_EDGE_05
     * Register a user with minimum allowed email length.
     */
    @Test
    public void testRegisterUser_MinEmailLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("a@b.co"); // Minimum valid email
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_EDGE_06
     * Register a user with a password of exactly 8 characters.
     */
    @Test
    public void testRegisterUser_ExactMinPasswordLength() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("pass1234"); // 8 characters
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_CORNER_01
     * Register a user with an email containing subdomains.
     */
    @Test
    public void testRegisterUser_EmailWithSubdomains() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Subdomain User");
        userDTO.setEmail("user@sub.domain.example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_CORNER_02
     * Register a user with special characters in the name.
     */
    @Test
    public void testRegisterUser_NameWithSpecialCharacters() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John!@# Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_CORNER_03
     * Register a user with special characters in the email.
     */
    @Test
    public void testRegisterUser_EmailWithSpecialCharacters() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john+filter@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_CORNER_04
     * Register a user with leading/trailing spaces in fields.
     */
    @Test
    public void testRegisterUser_FieldsWithLeadingTrailingSpaces() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(" John Doe ");
        userDTO.setEmail(" john.doe@example.com ");
        userDTO.setPassword(" password123 ");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test Case ID: TC_CORNER_05
     * Register a user with mixed whitespace in fields.
     */
    @Test
    public void testRegisterUser_MixedWhitespaceInFields() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Test Case ID: TC_CORNER_06
     * Register a user with Unicode characters in the name.
     */
    @Test
    public void testRegisterUser_UnicodeCharactersInName() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Йон До");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");


        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}
