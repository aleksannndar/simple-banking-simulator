package com.example.banking.domain;

import java.util.Objects;

public final class Account {
    private final AccountId id;
    private Money balance;

    private Account(AccountId id, Money balance) {
        this.id = id;
        this.balance = balance;
    }

    public static Account create(AccountId id, Money initialDeposit) {
        Objects.requireNonNull(id, "Account ID cannot be null");
        Objects.requireNonNull(initialDeposit, "Initial deposit cannot be null");

        if (initialDeposit.isNegative()) {
            throw new InvalidAmountException("Initial deposit cannot be negative");
        }

        return new Account(id, initialDeposit);
    }

    public void deposit(Money amount) {
        validateTransactionAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        validateTransactionAmount(amount);
        if (amount.isGreaterThan(balance)) {
            throw new InsufficientFundsException(balance, amount);
        }

        balance = balance.subtract(amount);
    }

    public AccountId id() {
        return id;
    }

    public Money balance() {
        return balance;
    }

    public CurrencyCode currency() {
        return balance.currency();
    }

    private void validateTransactionAmount(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (!amount.isPositive()) {
            throw new InvalidAmountException("Transaction amount must be positive");
        }
    }
}
