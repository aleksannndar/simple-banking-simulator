package com.example.banking.transaction;

import java.util.function.Supplier;

@FunctionalInterface
public interface TransactionManager {
    /**
     * Executes a write operation within an implementation-defined transaction boundary.
     */
    <T> T execute(Supplier<T> operation);
}
