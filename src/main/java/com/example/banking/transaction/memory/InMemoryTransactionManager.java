package com.example.banking.transaction.memory;

import com.example.banking.transaction.TransactionManager;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Serializes writes within one application instance. It does not provide rollback.
 */
public final class InMemoryTransactionManager implements TransactionManager {
    private final Lock writeLock = new ReentrantLock();

    @Override
    public <T> T execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "Operation cannot be null");

        writeLock.lock();
        try {
            return operation.get();
        } finally {
            writeLock.unlock();
        }
    }
}
