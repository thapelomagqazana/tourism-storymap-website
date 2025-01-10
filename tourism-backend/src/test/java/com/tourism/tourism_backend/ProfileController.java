package com.tourism.tourism_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.BlacklistedTokenRepository;
import com.tourism.tourism_backend.repositories.UserRepository;
import com.tourism.tourism_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileController{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String validTokenJohnDoe;
    private String validTokenJaneDoe;
    private String validTokenComplexName;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll(); // Clear the users table before each test
        blacklistedTokenRepository.deleteAll();
    
        // Create and save users with unique emails
        AppUser userJohn = new AppUser("John Doe", "john.doe@example.com", "password123");
        userRepository.save(userJohn);
        UserDetails userDetails = new User(userJohn.getEmail(), userJohn.getPassword(), Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        validTokenJohnDoe = jwtUtil.generateToken(userJohn.getEmail());
    
        // AppUser userJane = new AppUser("Jane Doe", "jane.doe@example.net", "password123"); // Changed email
        // userRepository.save(userJane);
        // validTokenJaneDoe = jwtUtil.generateToken(userJane.getEmail());
    
        // AppUser userComplex = new AppUser("John@#Doe123", "john.complex@example.org", "password123"); // Changed email
        // userRepository.save(userComplex);
        // validTokenComplexName = jwtUtil.generateToken(userComplex.getEmail());
        
    }
    

    /**
     * Test Case ID: TC_POS_01
     * Description: Retrieve profile of a valid logged-in user.
     */
    @Test
    public void testRetrieveProfile_ValidUser() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + validTokenJohnDoe)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test Case ID: TC_POS_02
     * Description: Retrieve profile after login.
     */
    // @Test
    // public void testRetrieveProfile_AfterLogin() throws Exception {
    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + validTokenJaneDoe)
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.name").value("Jane Doe"))
    //             .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    // }

    /**
     * Test Case ID: TC_POS_03
     * Description: Retrieve profile with a user having a complex name.
     */
    // @Test
    // public void testRetrieveProfile_ComplexNameUser() throws Exception {
    //     mockMvc.perform(get("/api/users/profile")
    //             .header("Authorization", "Bearer " + validTokenComplexName)
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.name").value("John@#Doe123"))
    //             .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    // }
}
