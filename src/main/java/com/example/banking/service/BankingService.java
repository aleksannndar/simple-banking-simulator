package com.example.banking.service;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;

public interface BankingService {
    AccountId createAccount(Money initialDeposit);

    Money deposit(AccountId accountId, Money amount);

    Money withdraw(AccountId accountId, Money amount);

    TransferResult transfer(TransferRequest request);

    Money getBalance(AccountId accountId);
}
