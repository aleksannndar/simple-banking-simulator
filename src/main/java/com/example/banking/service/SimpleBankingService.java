package com.example.banking.service;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountRepository;

import java.util.Objects;

public final class SimpleBankingService implements BankingService {
    private final AccountRepository accountRepository;

    public SimpleBankingService(AccountRepository accountRepository) {
        Objects.requireNonNull(accountRepository, "Account Repository cannot be null");
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountId createAccount(Money initialDeposit) {
        Account newAccount = Account.create(AccountId.generate(), initialDeposit);
        accountRepository.save(newAccount);
        return newAccount.id();
    }

    @Override
    public Money deposit(AccountId accountId, Money amount) {
        Account account = accountRepository.get(accountId);
        account.deposit(amount);
        accountRepository.save(account);
        return account.balance();
    }

    @Override
    public Money withdraw(AccountId accountId, Money amount) {
        Account account = accountRepository.get(accountId);
        account.withdraw(amount);
        accountRepository.save(account);
        return account.balance();
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
