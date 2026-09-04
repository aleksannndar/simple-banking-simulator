package com.example.banking;

import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.memory.InMemoryAccountRepository;
import com.example.banking.service.BankingService;
import com.example.banking.service.SimpleBankingService;
import com.example.banking.transaction.TransactionManager;
import com.example.banking.transaction.memory.InMemoryTransactionManager;

public final class BankingServiceFactory {
    private BankingServiceFactory() {
    }

    public static BankingService createInMemory() {
        AccountRepository accountRepository = new InMemoryAccountRepository();
        TransactionManager transactionManager = new InMemoryTransactionManager();

        return new SimpleBankingService(accountRepository, transactionManager);
    }
}
