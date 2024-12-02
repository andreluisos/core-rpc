package message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SequentialMessageIdGeneratorTest {

    private MessageIdGenerator sequentialMessageIdGenerator;

    @BeforeEach
    public void setUp() {
        this.sequentialMessageIdGenerator = new SequentialMessageIdGenerator();
    }

    @Test
    public void startsFromOne() {
        // Given a fresh generator
        // Next id should generate 1
        var firstId = sequentialMessageIdGenerator.nextId();
        assertEquals(1, firstId);
    }

    @Test
    public void increasesByOne() {
        // Given a fresh generator
        for (int i = 1; i < 100; i++) {
            // Each id should be 1 higher than last one
            assertEquals(i, sequentialMessageIdGenerator.nextId());
        }
    }

    @Test
    public void worksInMultithreadedEnvironment() throws InterruptedException {
        int repeatCount = 25000;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        try {
            Set<Integer> integers = ConcurrentHashMap.newKeySet();
            CountDownLatch countDownLatch = new CountDownLatch(repeatCount);
            for (int i = 0; i < repeatCount; i++) {
                executorService.submit(
                        () -> {
                            integers.add(sequentialMessageIdGenerator.nextId());
                            countDownLatch.countDown();
                        });
            }
            // Wait for all tasks to complete
            if (!countDownLatch.await(60, TimeUnit.SECONDS)) {
                fail("Timeout");
            }
            // Shutdown executor service and wait for tasks to finish
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                fail("Executor service did not terminate in the allotted time.");
            }
            // Verify no duplicates and correct sequencing
            assertEquals(repeatCount, integers.size());
            assertEquals(repeatCount + 1, sequentialMessageIdGenerator.nextId());
            List<Integer> sortedIds = new ArrayList<>(integers);
            Collections.sort(sortedIds);
            for (int i = 1; i <= repeatCount; i++) {
                assertEquals(i, sortedIds.get(i - 1).intValue());
            }
        } finally {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }
}
