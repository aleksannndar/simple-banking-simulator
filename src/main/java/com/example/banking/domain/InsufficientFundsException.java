package com.example.banking.domain;

import java.io.Serial;

public final class InsufficientFundsException extends IllegalStateException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(Money balance, Money requested) {
        super("Insufficient funds: balance is " + balance + ", requested " + requested);
    }
}
