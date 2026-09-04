package com.example.banking.service;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountRepository;
import com.example.banking.transaction.TransactionManager;

import java.util.Objects;

public final class SimpleBankingService implements BankingService {
    private final AccountRepository accountRepository;
    private final TransactionManager transactionManager;

    public SimpleBankingService(
            AccountRepository accountRepository,
            TransactionManager transactionManager
    ) {
        this.accountRepository = Objects.requireNonNull(
                accountRepository,
                "Account repository cannot be null"
        );
        this.transactionManager = Objects.requireNonNull(
                transactionManager,
                "Transaction manager cannot be null"
        );
    }

    @Override
    public AccountId createAccount(Money initialDeposit) {
        Objects.requireNonNull(initialDeposit, "Initial deposit cannot be null");

        return transactionManager.execute(() -> {
            Account newAccount = Account.create(AccountId.generate(), initialDeposit);
            accountRepository.save(newAccount);
            return newAccount.id();
        });
    }

    @Override
    public Money deposit(AccountId accountId, Money amount) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        return transactionManager.execute(() -> {
            Account updatedAccount = accountRepository.getForUpdate(accountId).deposit(amount);
            accountRepository.save(updatedAccount);
            return updatedAccount.balance();
        });
    }

    @Override
    public Money withdraw(AccountId accountId, Money amount) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        return transactionManager.execute(() -> {
            Account updatedAccount = accountRepository.getForUpdate(accountId).withdraw(amount);
            accountRepository.save(updatedAccount);
            return updatedAccount.balance();
        });
    }

    @Override
    public TransferResult transfer(TransferRequest request) {
        Objects.requireNonNull(request, "Transfer request cannot be null");

        AccountId source = request.sourceAccountId();
        AccountId destination = request.destinationAccountId();
        Money amount = request.amount();

        return transactionManager.execute(() -> {
            Account sourceAccount = accountRepository.getForUpdate(source);
            Account destinationAccount = accountRepository.getForUpdate(destination);
            Account updatedSourceAccount = sourceAccount.withdraw(amount);
            Account updatedDestinationAccount = destinationAccount.deposit(amount);

            accountRepository.save(updatedSourceAccount);
            accountRepository.save(updatedDestinationAccount);

            return new TransferResult(
                    source,
                    destination,
                    amount,
                    updatedSourceAccount.balance(),
                    updatedDestinationAccount.balance()
            );
        });
    }

    @Override
    public Money getBalance(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");

        return accountRepository.get(accountId).balance();
    }
}
