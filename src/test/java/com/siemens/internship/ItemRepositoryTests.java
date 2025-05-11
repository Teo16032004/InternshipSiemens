package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.siemens.internship.Item;
import com.siemens.internship.ItemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest // For testing JPA repositories
class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    private Item createAndSaveItem(String name, String description, String status, String email) {
        Item item = new Item(null, name, description, status, email);
        return itemRepository.save(item);
    }

    @Test
    void findAllIds_ReturnsAllItemIds() {
        Item item1 = createAndSaveItem("Test 1", "Desc 1", "NEW", "test1@example.com");
        Item item2 = createAndSaveItem("Test 2", "Desc 2", "NEW", "test2@example.com");

        List<Long> ids = itemRepository.findAllIds();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(item1.getId()));
        assertTrue(ids.contains(item2.getId()));
    }
}