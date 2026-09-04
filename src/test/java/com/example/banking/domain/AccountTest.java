package com.example.banking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {
    private static final AccountId ACCOUNT_ID = new AccountId(
            UUID.fromString("3c945454-59e4-49b3-96db-a96550c5e994")
    );

    @Test
    void createsAccountWithInitialDeposit() {
        Money initialDeposit = euros(10_00);

        Account account = Account.create(ACCOUNT_ID, initialDeposit);

        assertThat(account.id()).isEqualTo(ACCOUNT_ID);
        assertThat(account.balance()).isEqualTo(initialDeposit);
        assertThat(account.currency()).isEqualTo(CurrencyCode.EUR);
    }

    @Test
    void permitsZeroInitialDeposit() {
        Account account = Account.create(ACCOUNT_ID, Money.zero(CurrencyCode.EUR));

        assertThat(account.balance()).isEqualTo(euros(0));
    }

    @Test
    void rejectsNegativeInitialDeposit() {
        assertThatThrownBy(() -> Account.create(ACCOUNT_ID, euros(-1)))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Initial deposit cannot be negative");
    }

    @Test
    void depositsMoney() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        account.deposit(euros(2_50));

        assertThat(account.balance()).isEqualTo(euros(12_50));
    }

    @Test
    void rejectsNonPositiveDeposit() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        assertThatThrownBy(() -> account.deposit(euros(0)))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.deposit(euros(-1)))
                .isInstanceOf(InvalidAmountException.class);
        assertThat(account.balance()).isEqualTo(euros(10_00));
    }

    @Test
    void withdrawsMoney() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        account.withdraw(euros(3_25));

        assertThat(account.balance()).isEqualTo(euros(6_75));
    }

    @Test
    void permitsWithdrawingEntireBalance() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        account.withdraw(euros(10_00));

        assertThat(account.balance()).isEqualTo(euros(0));
    }

    @Test
    void rejectsNonPositiveWithdrawal() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        assertThatThrownBy(() -> account.withdraw(euros(0)))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.withdraw(euros(-1)))
                .isInstanceOf(InvalidAmountException.class);
        assertThat(account.balance()).isEqualTo(euros(10_00));
    }

    @Test
    void preventsOverdraftWithoutChangingBalance() {
        Account account = Account.create(ACCOUNT_ID, euros(10_00));

        assertThatThrownBy(() -> account.withdraw(euros(10_01)))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(account.balance()).isEqualTo(euros(10_00));
    }

    @Test
    void rejectsMissingCreationValues() {
        assertThatThrownBy(() -> Account.create(null, euros(10_00)))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Account.create(ACCOUNT_ID, null))
                .isInstanceOf(NullPointerException.class);
    }

    private static Money euros(long minorUnits) {
        return Money.ofMinor(minorUnits, CurrencyCode.EUR);
    }
}
