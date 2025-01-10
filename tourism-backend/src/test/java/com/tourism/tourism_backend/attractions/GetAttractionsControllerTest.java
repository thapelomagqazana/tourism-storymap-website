package com.tourism.tourism_backend.attractions;

import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetAttractionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    @BeforeEach
    public void setup() {
        attractionRepository.deleteAll();
    }

    /**
     * TC_POS_01: Retrieve attractions when the list is not empty.
     */
    @Test
    public void testRetrieveAttractions_ListNotEmpty() throws Exception {
        // Add sample attractions to the repository
        attractionRepository.save(new Attraction("Attraction 1", "Description 1"));
        attractionRepository.save(new Attraction("Attraction 2", "Description 2"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Attraction 1"))
                .andExpect(jsonPath("$[1].name").value("Attraction 2"));
    }

    /**
     * TC_POS_02: Retrieve attractions when the list is empty.
     */
    @Test
    public void testRetrieveAttractions_ListEmpty() throws Exception {
        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * TC_POS_03: Retrieve attractions with exactly 1 entry.
     */
    @Test
    public void testRetrieveAttractions_OneEntry() throws Exception {
        // Add one sample attraction to the repository
        attractionRepository.save(new Attraction("Single Attraction", "Single Description"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Single Attraction"))
                .andExpect(jsonPath("$[0].shortDescription").value("Single Description"));
    }

    /**
     * TC_POS_04: Retrieve attractions with exactly 100 entries.
     */
    @Test
    public void testRetrieveAttractions_HundredEntries() throws Exception {
        // Add 100 sample attractions to the repository
        List<Attraction> attractions = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            attractions.add(new Attraction("Attraction " + i, "Description " + i));
        }
        attractionRepository.saveAll(attractions);

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100))
                .andExpect(jsonPath("$[99].name").value("Attraction 100"))
                .andExpect(jsonPath("$[99].shortDescription").value("Description 100"));
    }

    /**
     * TC_NEG_01: Attempt to retrieve attractions with invalid method.
     */
    @Test
    public void testRetrieveAttractions_InvalidMethod() throws Exception {
        mockMvc.perform(post("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("Method not allowed"));
    }

    // /**
    //  * TC_NEG_02: Attempt to retrieve attractions with incorrect URL.
    //  */
    // @Test
    // public void testRetrieveAttractions_IncorrectURL() throws Exception {
    //     mockMvc.perform(get("/api/attractionsXYZ")
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.error").value("Endpoint not found"));
    // }

    // /**
    //  * TC_NEG_03: Attempt to retrieve attractions without authorization (if applicable).
    //  */
    // @Test
    // public void testRetrieveAttractions_NoAuthorization() throws Exception {
    //     mockMvc.perform(get("/api/attractions")
    //             .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.error").value("Authentication required"));
    // }

    /**
     * TC_EDGE_01: Retrieve attractions when the list has the maximum allowed entries (e.g., 10,000).
     */
    @Test
    public void testRetrieveAttractions_MaxEntries() throws Exception {
        List<Attraction> attractions = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            attractions.add(new Attraction("Attraction " + i, "Description " + i));
        }
        attractionRepository.saveAll(attractions);

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10000));
    }

    /**
     * TC_EDGE_02: Retrieve attractions when an entry has the maximum allowed name length (255 characters).
     */
    @Test
    public void testRetrieveAttractions_MaxNameLength() throws Exception {
        String maxName = "A".repeat(255);
        attractionRepository.save(new Attraction(maxName, "Description with normal length"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(maxName));
    }

    /**
     * TC_EDGE_03: Retrieve attractions when an entry has the maximum allowed short description length (500 characters).
     */
    @Test
    public void testRetrieveAttractions_MaxShortDescriptionLength() throws Exception {
        String maxDescription = "D".repeat(500);
        attractionRepository.save(new Attraction("Normal Name", maxDescription));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].shortDescription").value(maxDescription));
    }

    /**
     * TC_EDGE_04: Retrieve attractions when all entries have the same name and description.
     */
    @Test
    public void testRetrieveAttractions_AllSameEntries() throws Exception {
        List<Attraction> attractions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            attractions.add(new Attraction("Same Name", "Same Description"));
        }
        attractionRepository.saveAll(attractions);

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].name").value("Same Name"))
                .andExpect(jsonPath("$[0].shortDescription").value("Same Description"));
    }

    /**
     * TC_CORNER_01: Retrieve attractions with Unicode characters in the name.
     */
    @Test
    public void testRetrieveAttractions_UnicodeName() throws Exception {
        attractionRepository.save(new Attraction("Аттракцион", "Описание"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Аттракцион"))
                .andExpect(jsonPath("$[0].shortDescription").value("Описание"));
    }

    /**
     * TC_CORNER_02: Retrieve attractions with special characters in the name.
     */
    @Test
    public void testRetrieveAttractions_SpecialCharactersInName() throws Exception {
        attractionRepository.save(new Attraction("Attraction@#%!", "Short description with special characters"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Attraction@#%!"));
    }

    /**
     * TC_CORNER_03: Retrieve attractions with leading/trailing spaces in the name.
     */
    @Test
    public void testRetrieveAttractions_LeadingTrailingSpaces() throws Exception {
        attractionRepository.save(new Attraction("  Leading Space  ", "Short description"));

        mockMvc.perform(get("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("  Leading Space  "));
    }
}
