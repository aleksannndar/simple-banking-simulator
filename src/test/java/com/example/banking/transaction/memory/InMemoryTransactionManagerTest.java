package com.example.banking.transaction.memory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class InMemoryTransactionManagerTest {
    private final InMemoryTransactionManager transactionManager =
            new InMemoryTransactionManager();

    @Test
    void returnsTheOperationResult() {
        int result = transactionManager.execute(() -> 42);

        assertThat(result).isEqualTo(42);
    }

    @Test
    void serializesConcurrentOperations() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch firstOperationEntered = new CountDownLatch(1);
        CountDownLatch releaseFirstOperation = new CountDownLatch(1);
        CountDownLatch secondOperationStarted = new CountDownLatch(1);
        CountDownLatch secondOperationEntered = new CountDownLatch(1);

        try {
            Future<?> firstOperation = executor.submit(() -> transactionManager.execute(() -> {
                firstOperationEntered.countDown();
                await(releaseFirstOperation);
                return null;
            }));
            assertThat(firstOperationEntered.await(1, SECONDS)).isTrue();

            Future<?> secondOperation = executor.submit(() -> {
                secondOperationStarted.countDown();
                return transactionManager.execute(() -> {
                    secondOperationEntered.countDown();
                    return null;
                });
            });
            assertThat(secondOperationStarted.await(1, SECONDS)).isTrue();

            assertThat(secondOperationEntered.await(100, MILLISECONDS)).isFalse();

            releaseFirstOperation.countDown();
            firstOperation.get(1, SECONDS);
            secondOperation.get(1, SECONDS);
            assertThat(secondOperationEntered.getCount()).isZero();
        } finally {
            releaseFirstOperation.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void releasesTheLockWhenAnOperationFails() throws Exception {
        assertThatThrownBy(() -> transactionManager.execute(() -> {
            throw new IllegalStateException();
        })).isInstanceOf(IllegalStateException.class);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> nextOperation =
                    executor.submit(() -> transactionManager.execute(() -> 42));

            assertThat(nextOperation.get(1, SECONDS)).isEqualTo(42);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void rejectsNullOperations() {
        assertThatNullPointerException()
                .isThrownBy(() -> transactionManager.execute(null));
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(1, SECONDS)) {
                throw new AssertionError("Timed out while waiting for test coordination");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError(exception);
        }
    }
}
