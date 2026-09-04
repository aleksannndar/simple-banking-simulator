package com.example.banking.service;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;

public final class SimpleBankingService implements BankingService {
    @Override
    public AccountId createAccount(Money initialDeposit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Money deposit(AccountId accountId, Money amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Money withdraw(AccountId accountId, Money amount) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TransferResult transfer(TransferRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Money getBalance(AccountId accountId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
