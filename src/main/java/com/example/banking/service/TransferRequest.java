package com.example.banking.service;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;
import java.util.Objects;

public record TransferRequest(
        AccountId sourceAccountId,
        AccountId destinationAccountId,
        Money amount
) {
    public TransferRequest {
        Objects.requireNonNull(sourceAccountId, "Source account ID cannot be null");
        Objects.requireNonNull(destinationAccountId, "Destination account ID cannot be null");
        Objects.requireNonNull(amount, "Transfer amount cannot be null");
    }
}
