package com.example.banking.domain;

import java.io.Serial;

public final class InvalidAmountException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidAmountException(String message) {
        super(message);
    }
}
