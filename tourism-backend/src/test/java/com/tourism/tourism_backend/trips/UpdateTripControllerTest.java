package com.tourism.tourism_backend.trips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.TripRepository;
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
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UpdateTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminJwtToken, userJwtToken;
    private List<Long> attractionIds;

    private Long tripId1, tripId2, tripId3;

    @BeforeEach
    public void setup() throws Exception {
        // Add sample attractions
        Attraction attraction1 = new Attraction("Attraction 1", "Description 1", 10.0, List.of("photo1"));
        Attraction attraction2 = new Attraction("Attraction 2", "Description 2", 15.0, List.of("photo2"));

        List<Attraction> savedAttractions = attractionRepository.saveAll(List.of(attraction1, attraction2));

        // Retrieve the IDs of saved attractions
        attractionIds = savedAttractions.stream().map(Attraction::getId).collect(Collectors.toList());
    
        // Add sample trips and store their IDs
        Trip trip1 = tripRepository.save(new Trip("Trip 1", List.of("Day 1", "Day 2"), List.of(attraction1, attraction2)));
        Trip trip2 = tripRepository.save(new Trip("Trip 2", List.of("Day 1"), List.of(attraction1)));
        Trip trip3 = tripRepository.save(new Trip("Trip 3", List.of("Day 1", "Day 2"), List.of()));
    
        tripId1 = trip1.getId();
        tripId2 = trip2.getId();
        tripId3 = trip3.getId();
    
        // Create an admin user and get their token
        userRepository.save(new AppUser("Admin", "admin@example.com", new BCryptPasswordEncoder().encode("admin123"), "ADMIN"));
        adminJwtToken = "Bearer " + obtainJwtToken("admin@example.com", "admin123");

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
    public void testUpdateTrip_ValidData() throws Exception {
        String requestBody = """
            {
                "name": "Updated Trip",
                "duration": ["Day 1", "Day 2"],
                "attractionIds": [%d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(1));
    
        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Trip"))
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.attractions").isArray());
    }
    
    @Test
    public void testUpdateTrip_SingleAttraction() throws Exception {
        String requestBody = """
            {
                "name": "Single Attraction Trip",
                "duration": ["Day 1"],
                "attractionIds": [%d]
            }
        """.formatted(attractionIds.get(0));
    
        mockMvc.perform(put("/api/trips/" + tripId2)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Single Attraction Trip"))
                .andExpect(jsonPath("$.attractions.length()").value(1));
    }
    
    @Test
    public void testUpdateTrip_NoAttractions() throws Exception {
        String requestBody = """
            {
                "name": "No Attractions Trip",
                "duration": ["Day 1", "Day 2"],
                "attractionIds": []
            }
        """;
    
        mockMvc.perform(put("/api/trips/" + tripId3)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("No Attractions Trip"))
                .andExpect(jsonPath("$.attractions").isEmpty());
    }

    @Test
    public void testUpdateTrip_EmptyAttractions() throws Exception {
        String requestBody = """
            {
                "name": "Empty Attractions",
                "duration": ["Day 1"],
                "attractionIds": []
            }
        """;

        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Empty Attractions"))
                .andExpect(jsonPath("$.attractions").isEmpty());
    }

    @Test
    public void testUpdateTrip_MaxNameLength() throws Exception {
        String longName = "A".repeat(255); // Generate 255-character long name
        String requestBody = """
            {
                "name": "%s",
                "duration": ["Day 1"],
                "attractionIds": [%d, %d]
            }
        """.formatted(longName, attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(longName));
    }

    @Test
    public void testUpdateTrip_LargeDurationArray() throws Exception {
        List<String> days = List.of("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7", "Day 8", "Day 9", "Day 10");
        String requestBody = """
            {
                "name": "Long Duration Trip",
                "duration": %s,
                "attractionIds": [%d, %d]
            }
        """.formatted(new ObjectMapper().writeValueAsString(days), attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Long Duration Trip"))
                .andExpect(jsonPath("$.days.length()").value(10));
    }

    @Test
    public void testUpdateTrip_SpecialCharactersInName() throws Exception {
        String specialName = "@#$%^&*()_+{}:<>?";
        String requestBody = """
            {
                "name": "%s",
                "duration": ["Day 1"],
                "attractionIds": [%d, %d]
            }
        """.formatted(specialName, attractionIds.get(0), attractionIds.get(1));

        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(specialName));
    }

    @Test
    public void testUpdateTrip_DuplicateAttractions() throws Exception {
        String requestBody = """
            {
                "name": "Duplicate Attractions",
                "duration": ["Day 1"],
                "attractionIds": [%d, %d, %d, %d]
            }
        """.formatted(attractionIds.get(0), attractionIds.get(0), attractionIds.get(1), attractionIds.get(1));

        mockMvc.perform(put("/api/trips/" + tripId1)
                .header("Authorization", adminJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Duplicate Attractions"))
                .andExpect(jsonPath("$.attractions.length()").value(2)); // Duplicates removed
    }

    // @Test
    // public void testUpdateTrip_LargeNumberOfAttractions() throws Exception {
    //     List<Long> manyAttractionIds = attractionIds.stream().limit(1000).collect(Collectors.toList()); // Simulate 1000 attractions
    //     String requestBody = """
    //         {
    //             "name": "Many Attractions",
    //             "duration": ["Day 1"],
    //             "attractionIds": %s
    //         }
    //     """.formatted(new ObjectMapper().writeValueAsString(manyAttractionIds));

    //     mockMvc.perform(put("/api/trips/" + tripId1)
    //             .header("Authorization", adminJwtToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.name").value("Many Attractions"))
    //             .andExpect(jsonPath("$.attractions.length()").value(1000));
    // }

}
