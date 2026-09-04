package com.example.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.CurrencyCode;
import com.example.banking.domain.InvalidAmountException;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.memory.InMemoryAccountRepository;
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
}
