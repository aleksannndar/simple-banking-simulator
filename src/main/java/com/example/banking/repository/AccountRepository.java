package com.example.banking.repository;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import java.util.Optional;

public interface AccountRepository {
    void save(Account account);

    Optional<Account> findById(AccountId accountId);
}
