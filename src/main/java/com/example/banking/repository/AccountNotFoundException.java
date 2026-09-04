package com.example.banking.repository;

import com.example.banking.domain.AccountId;
import java.io.Serial;
import java.util.NoSuchElementException;

public final class AccountNotFoundException extends NoSuchElementException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(AccountId accountId) {
        super("Account not found: " + accountId);
    }
}
