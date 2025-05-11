package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger; // Import AtomicInteger
import java.util.Collections; // Import Collections

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool

    // Using a thread-safe list
    private final List<Item> processedItems = Collections.synchronizedList(new ArrayList<>());

    // Using AtomicInteger for thread-safe counting
    private final AtomicInteger processedCount = new AtomicInteger(0);

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Refactored processItemsAsync function to address concurrency, error handling,
     * and asynchronous execution issues.
     *
     * Changes:
     * - Removed @Async annotation as it's not needed and doesn't work as intended here.
     * - Used a ThreadPoolExecutor for managing threads.
     * - Used CompletableFuture.supplyAsync() to perform asynchronous tasks and collect results.
     * - Handled exceptions within the CompletableFuture and propagated them.
     * - Used a thread-safe list (Collections.synchronizedList) and AtomicInteger for shared state.
     * - Ensured all items are processed before returning the result.
     * - Used Transactional annotation to ensure each update is atomic.
     */
    @Transactional // Ensure each update is atomic
    public List<Item> processItemsAsync() {
        List<Long> itemIds = itemRepository.findAllIds();
        List<CompletableFuture<Item>> futures = new ArrayList<>();

        for (Long id : itemIds) {
            CompletableFuture<Item> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(100); // Simulate processing time

                    Item item = itemRepository.findById(id).orElse(null);
                    if (item == null) {
                        return null; // Or throw an exception if null items are an error
                    }

                    item.setStatus("PROCESSED");
                    itemRepository.save(item);

                    processedCount.incrementAndGet();
                    processedItems.add(item);
                    return item;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    throw new RuntimeException("Error processing item with id: " + id, e);
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all futures to complete and handle exceptions
        List<Item> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(item -> item != null) // Filter out null results (if any)
                .collect(Collectors.toList());

        System.out.println("Total items processed: " + processedCount.get());
        return results;
    }
}