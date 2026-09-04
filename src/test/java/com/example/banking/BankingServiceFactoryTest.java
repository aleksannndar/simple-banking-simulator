package com.example.banking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.banking.domain.AccountId;
import com.example.banking.domain.CurrencyCode;
import com.example.banking.domain.Money;
import com.example.banking.repository.AccountNotFoundException;
import com.example.banking.service.BankingService;
import org.junit.jupiter.api.Test;

class BankingServiceFactoryTest {
    @Test
    void createsIndependentInMemoryBankingServices() {
        BankingService firstService = BankingServiceFactory.createInMemory();
        BankingService secondService = BankingServiceFactory.createInMemory();
        AccountId accountId = firstService.createAccount(euros(1_000));

        assertThat(firstService.getBalance(accountId)).isEqualTo(euros(1_000));
        assertThatThrownBy(() -> secondService.getBalance(accountId))
                .isInstanceOf(AccountNotFoundException.class);
    }

    private static Money euros(long minorUnits) {
        return Money.ofMinor(minorUnits, CurrencyCode.EUR);
    }
}
