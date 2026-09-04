package com.example.banking.repository.memory;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import com.example.banking.repository.AccountRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryAccountRepository implements AccountRepository {
    private final ConcurrentMap<AccountId, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void save(Account account) {
        Objects.requireNonNull(account, "Account cannot be null");
        accounts.put(account.id(), account);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        return Optional.ofNullable(accounts.get(accountId));
    }
}
