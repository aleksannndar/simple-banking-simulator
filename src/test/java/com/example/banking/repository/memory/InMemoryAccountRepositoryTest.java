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

        assertThat(repository.findById(ACCOUNT_ID)).hasValueSatisfying(savedAccount -> {
            assertThat(savedAccount.id()).isEqualTo(ACCOUNT_ID);
            assertThat(savedAccount.balance()).isEqualTo(Money.ofMinor(1_000, CurrencyCode.EUR));
        });
    }

    @Test
    void getsAccountById() {
        Account account = accountWithBalance(1_000);
        repository.save(account);

        assertThat(repository.get(ACCOUNT_ID).balance())
                .isEqualTo(Money.ofMinor(1_000, CurrencyCode.EUR));
    }

    @Test
    void getsAccountForUpdateById() {
        Account account = accountWithBalance(1_000);
        repository.save(account);

        assertThat(repository.getForUpdate(ACCOUNT_ID).balance())
                .isEqualTo(Money.ofMinor(1_000, CurrencyCode.EUR));
    }

    @Test
    void returnsEmptyWhenAccountDoesNotExist() {
        assertThat(repository.findById(ACCOUNT_ID)).isEmpty();
    }

    @Test
    void throwsWhenGettingAccountThatDoesNotExist() {
        assertThatThrownBy(() -> repository.get(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
        assertThatThrownBy(() -> repository.getForUpdate(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void replacesPreviouslySavedAccountWithSameId() {
        Account original = accountWithBalance(1000);
        Account replacement = accountWithBalance(2000);

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.get(ACCOUNT_ID).balance())
                .isEqualTo(Money.ofMinor(2_000, CurrencyCode.EUR));
    }

    @Test
    void makesAccountChangesVisibleOnlyAfterSavingUpdatedSnapshot() {
        Account savedAccount = accountWithBalance(1_000);
        repository.save(savedAccount);

        Account updatedAccount = repository.get(ACCOUNT_ID).deposit(Money.ofMinor(500, CurrencyCode.EUR));

        assertThat(repository.get(ACCOUNT_ID).balance())
                .isEqualTo(Money.ofMinor(1_000, CurrencyCode.EUR));

        repository.save(updatedAccount);

        assertThat(repository.get(ACCOUNT_ID).balance())
                .isEqualTo(Money.ofMinor(1_500, CurrencyCode.EUR));
    }

    @Test
    void rejectsNullValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> repository.save(null));
        assertThatNullPointerException()
                .isThrownBy(() -> repository.findById(null));
        assertThatNullPointerException()
                .isThrownBy(() -> repository.get(null));
        assertThatNullPointerException()
                .isThrownBy(() -> repository.getForUpdate(null));
    }

    private static Account accountWithBalance(long minorUnits) {
        return Account.create(
                ACCOUNT_ID,
                Money.ofMinor(minorUnits, CurrencyCode.EUR)
        );
    }
}
