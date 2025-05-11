package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.siemens.internship.Item;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc // To use MockMvc
class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // To convert objects to JSON

    @Test
    void getAllItems() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/items"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void createItem_ValidInput_ReturnsCreated() throws Exception {
        Item newItem = new Item(null, "Test Item", "Description", "NEW", "test@example.com");
        String itemJson = objectMapper.writeValueAsString(newItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void createItem_InvalidInput_ReturnsBadRequest() throws Exception {
        Item invalidItem = new Item(null, null, null, null, "invalid-email"); // Invalid email
        String itemJson = objectMapper.writeValueAsString(invalidItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    void getItemById_ExistingId_ReturnsOk() throws Exception {
        // Assuming you have a way to pre-populate data (e.g., using a TestEntityManager or similar)
        // For simplicity, I'm skipping the data setup here, but in a real test, you'd ensure
        // an item with a specific ID exists.
        long existingItemId = 1L; // Replace with a valid ID

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/" + existingItemId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void getItemById_NonExistingId_ReturnsNoContent() throws Exception {
        long nonExistingItemId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/" + nonExistingItemId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    void updateItem_ExistingId_ReturnsOk() throws Exception {
        // Again, you'd need to set up data
        long existingItemId = 1L;
        Item updatedItem = new Item(existingItemId, "Updated Item", "Updated Description", "UPDATED", "updated@example.com");
        String itemJson = objectMapper.writeValueAsString(updatedItem);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/" + existingItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void updateItem_NonExistingId_ReturnsNotFound() throws Exception {
        long nonExistingItemId = 999L;
        Item updatedItem = new Item(nonExistingItemId, "Updated Item", "Updated Description", "UPDATED", "updated@example.com");
        String itemJson = objectMapper.writeValueAsString(updatedItem);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/" + nonExistingItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    void deleteItem_ExistingId_ReturnsNoContent() throws Exception {
        // Set up data
        long existingItemId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/items/" + existingItemId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    void processItems_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/process"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}