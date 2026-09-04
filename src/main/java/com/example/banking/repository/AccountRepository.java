package com.example.banking.repository;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import java.util.Objects;
import java.util.Optional;

public interface AccountRepository {
    void save(Account account);

    Optional<Account> findById(AccountId accountId);

    Account getForUpdate(AccountId accountId);

    default Account get(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        return findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
