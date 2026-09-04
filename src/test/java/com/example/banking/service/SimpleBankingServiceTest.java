package com.example.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.CurrencyCode;
import com.example.banking.domain.InsufficientFundsException;
import com.example.banking.domain.InvalidAmountException;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountNotFoundException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.memory.InMemoryAccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SimpleBankingServiceTest {
    private final AccountRepository repository = new InMemoryAccountRepository();
    private final SimpleBankingService service = new SimpleBankingService(repository);

    @Test
    void createsAndSavesAccountWithInitialDeposit() {
        Money initialDeposit = Money.ofMinor(1000, CurrencyCode.EUR);

        AccountId createdAccountId = service.createAccount(initialDeposit);

        assertThat(repository.get(createdAccountId).balance()).isEqualTo(initialDeposit);
    }

    @Test
    void permitsCreatingAccountWithZeroBalance() {
        AccountId createdAccountId = service.createAccount(Money.zero(CurrencyCode.EUR));

        assertThat(repository.get(createdAccountId).balance())
                .isEqualTo(Money.zero(CurrencyCode.EUR));
    }

    @Test
    void rejectsNegativeInitialDeposit() {
        Money negativeDeposit = Money.ofMinor(-1, CurrencyCode.EUR);

        assertThatThrownBy(() -> service.createAccount(negativeDeposit))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void rejectsNullInitialDeposit() {
        assertThatThrownBy(() -> service.createAccount(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullDepositArguments() {
        AccountId accountId = service.createAccount(euros(1000));

        assertThatThrownBy(() -> service.deposit(null, euros(100)))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.deposit(accountId, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullWithdrawalArguments() {
        AccountId accountId = service.createAccount(euros(1000));

        assertThatThrownBy(() -> service.withdraw(null, euros(100)))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.withdraw(accountId, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullTransferRequest() {
        assertThatThrownBy(() -> service.transfer(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullAccountIdWhenGettingBalance() {
        assertThatThrownBy(() -> service.getBalance(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void depositsAndReturnsResultingBalance() {
        AccountId accountId = service.createAccount(euros(1000));

        Money resultingBalance = service.deposit(accountId, euros(250));

        assertThat(resultingBalance).isEqualTo(euros(1250));
        assertThat(repository.get(accountId).balance()).isEqualTo(euros(1250));
    }

    @Test
    void withdrawsAndReturnsResultingBalance() {
        AccountId accountId = service.createAccount(euros(1000));

        Money resultingBalance = service.withdraw(accountId, euros(250));

        assertThat(resultingBalance).isEqualTo(euros(750));
        assertThat(repository.get(accountId).balance()).isEqualTo(euros(750));
    }

    @Test
    void rejectsDepositForMissingAccount() {
        AccountId missingAccountId = missingAccountId();

        assertThatThrownBy(() -> service.deposit(missingAccountId, euros(100)))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void rejectsWithdrawalForMissingAccount() {
        AccountId missingAccountId = missingAccountId();

        assertThatThrownBy(() -> service.withdraw(missingAccountId, euros(100)))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void rejectsNonPositiveDepositWithoutChangingBalance() {
        AccountId accountId = service.createAccount(euros(1000));

        assertThatThrownBy(() -> service.deposit(accountId, euros(0)))
                .isInstanceOf(InvalidAmountException.class);
        assertThat(repository.get(accountId).balance()).isEqualTo(euros(1000));
    }

    @Test
    void rejectsNonPositiveWithdrawalWithoutChangingBalance() {
        AccountId accountId = service.createAccount(euros(1000));

        assertThatThrownBy(() -> service.withdraw(accountId, euros(-1)))
                .isInstanceOf(InvalidAmountException.class);
        assertThat(repository.get(accountId).balance()).isEqualTo(euros(1000));
    }

    @Test
    void rejectsOverdraftWithoutChangingBalance() {
        AccountId accountId = service.createAccount(euros(1000));

        assertThatThrownBy(() -> service.withdraw(accountId, euros(1001)))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(repository.get(accountId).balance()).isEqualTo(euros(1000));
    }

    private static Money euros(long minorUnits) {
        return Money.ofMinor(minorUnits, CurrencyCode.EUR);
    }

    private static AccountId missingAccountId() {
        return new AccountId(
                UUID.fromString("00000000-0000-0000-0000-000000000001")
        );
    }
}
