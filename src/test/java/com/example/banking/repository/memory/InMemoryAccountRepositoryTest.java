package com.example.banking.repository.memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.banking.domain.Account;
import com.example.banking.domain.AccountId;
import com.example.banking.domain.CurrencyCode;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryAccountRepositoryTest {
    private static final AccountId ACCOUNT_ID = new AccountId(
            UUID.fromString("a066ea7e-6d61-45c7-a9d5-6bce862b4d60")
    );

    private final InMemoryAccountRepository repository = new InMemoryAccountRepository();

    @Test
    void savesAndFindsAccountById() {
        Account account = accountWithBalance(1_000);

        repository.save(account);

        assertThat(repository.findById(ACCOUNT_ID)).containsSame(account);
    }

    @Test
    void getsAccountById() {
        Account account = accountWithBalance(1_000);
        repository.save(account);

        assertThat(repository.get(ACCOUNT_ID)).isSameAs(account);
    }

    @Test
    void returnsEmptyWhenAccountDoesNotExist() {
        assertThat(repository.findById(ACCOUNT_ID)).isEmpty();
    }

    @Test
    void throwsWhenGettingAccountThatDoesNotExist() {
        assertThatThrownBy(() -> repository.get(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void replacesPreviouslySavedAccountWithSameId() {
        Account original = accountWithBalance(1000);
        Account replacement = accountWithBalance(2000);

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.findById(ACCOUNT_ID)).containsSame(replacement);
    }

    @Test
    void rejectsNullValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> repository.save(null));
        assertThatNullPointerException()
                .isThrownBy(() -> repository.findById(null));
        assertThatNullPointerException()
                .isThrownBy(() -> repository.get(null));
    }

    private static Account accountWithBalance(long minorUnits) {
        return Account.create(
                ACCOUNT_ID,
                Money.ofMinor(minorUnits, CurrencyCode.EUR)
        );
    }
}
