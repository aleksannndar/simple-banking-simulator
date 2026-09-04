package com.example.banking.service;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;
import java.util.Objects;

public record TransferResult(
        AccountId sourceAccountId,
        AccountId destinationAccountId,
        Money transferredAmount,
        Money sourceBalance,
        Money destinationBalance
) {
    public TransferResult {
        Objects.requireNonNull(sourceAccountId, "Source account ID cannot be null");
        Objects.requireNonNull(destinationAccountId, "Destination account ID cannot be null");
        Objects.requireNonNull(transferredAmount, "Transferred amount cannot be null");
        Objects.requireNonNull(sourceBalance, "Source balance cannot be null");
        Objects.requireNonNull(destinationBalance, "Destination balance cannot be null");
    }
}
