package com.example.banking.transaction;

import java.util.function.Supplier;

@FunctionalInterface
public interface TransactionManager {
    <T> T execute(Supplier<T> operation);
}
