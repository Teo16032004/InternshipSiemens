package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Rollback transactions after each test
class ItemServiceTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    // Helper method to create and save items for testing
    private Item createAndSaveItem(String name, String description, String status, String email) {
        Item item = new Item(null, name, description, status, email);
        return itemRepository.save(item);
    }

    @Test
    void processItemsAsync_ProcessesAllItems() throws InterruptedException {
        // Create and save some items for testing
        createAndSaveItem("Item 1", "Desc 1", "NEW", "item1@example.com");
        createAndSaveItem("Item 2", "Desc 2", "NEW", "item2@example.com");
        createAndSaveItem("Item 3", "Desc 3", "NEW", "item3@example.com");

        List<Item> processedItems = itemService.processItemsAsync();

        // Wait for processing to complete (you might need a more robust way to wait in real scenarios)
        Thread.sleep(2000); // Adjust the time as needed

        assertEquals(3, processedItems.size());
        assertEquals(3, itemRepository.findAll().size()); // Ensure all items are still in the database

        // Verify that all items are marked as "PROCESSED"
        itemRepository.findAll().forEach(item -> assertEquals("PROCESSED", item.getStatus()));
    }

    @Test
    void processItemsAsync_HandlesEmptyList() throws InterruptedException {
        List<Item> processedItems = itemService.processItemsAsync();
        Thread.sleep(1000);
        assertEquals(0, processedItems.size());
    }

    @Test
    void findAllItems() {
        createAndSaveItem("Item 4", "Desc 4", "NEW", "item4@example.com");
        createAndSaveItem("Item 5", "Desc 5", "NEW", "item5@example.com");

        List<Item> allItems = itemService.findAll();
        assertEquals(2, allItems.size());
    }

    @Test
    void findItemById() {
        Item savedItem = createAndSaveItem("Item 6", "Desc 6", "NEW", "item6@example.com");
        Item foundItem = itemService.findById(savedItem.getId()).orElse(null);
        assertNotNull(foundItem);
        assertEquals("Item 6", foundItem.getName());
    }

    @Test
    void saveItem() {
        Item itemToSave = new Item(null, "Item 7", "Desc 7", "NEW", "item7@example.com");
        Item savedItem = itemService.save(itemToSave);
        assertNotNull(savedItem.getId());
        assertEquals("Item 7", savedItem.getName());
    }

    @Test
    void deleteItemById() {
        Item itemToDelete = createAndSaveItem("Item 8", "Desc 8", "NEW", "item8@example.com");
        itemService.deleteById(itemToDelete.getId());
        assertFalse(itemRepository.findById(itemToDelete.getId()).isPresent());
    }

}