package com.tourism.tourism_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.models.User;
import com.tourism.tourism_backend.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    /**
     * Test for successful user registration.
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        // Create a valid UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password123");

        // Perform POST request and verify the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test for registration failure due to existing email.
     */
    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        // Create a user and save it to the database
        User existingUser = new User();
        existingUser.setName("Jane Doe");
        existingUser.setEmail("jane.doe@example.com");
        existingUser.setPassword("password123");
        userRepository.save(existingUser);

        // Create a UserDTO with the same email
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("jane.doe@example.com"); // Duplicate email
        userDTO.setPassword("password123");

        // Perform POST request and verify the response
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
        // Create an invalid UserDTO (missing password)
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword(""); // Empty password

        // Perform POST request and verify the response
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
