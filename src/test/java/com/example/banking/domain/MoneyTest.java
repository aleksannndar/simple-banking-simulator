package com.example.banking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {
    @Test
    void createsMoneyFromMajorUnits() {
        Money money = Money.ofMajor(new BigDecimal("12.34"), CurrencyCode.EUR);

        assertThat(money).isEqualTo(Money.ofMinor(1_234, CurrencyCode.EUR));
    }

    @Test
    void acceptsInsignificantTrailingZerosInMajorUnits() {
        Money money = Money.ofMajor(new BigDecimal("12.340"), CurrencyCode.EUR);

        assertThat(money.minorUnits()).isEqualTo(1_234);
    }

    @Test
    void rejectsUnsupportedFractionalPrecision() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Money.ofMajor(new BigDecimal("12.345"), CurrencyCode.EUR))
                .withMessageContaining("cannot be represented exactly");
    }

    @Test
    void rejectsAmountsOutsideLongRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Money.ofMajor(
                        new BigDecimal("92233720368547758.08"),
                        CurrencyCode.EUR
                ))
                .withMessageContaining("cannot be represented exactly");
    }

    @Test
    void createsZeroForCurrency() {
        assertThat(Money.zero(CurrencyCode.EUR))
                .isEqualTo(Money.ofMinor(0, CurrencyCode.EUR));
    }

    @Test
    void rejectsNullValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMajor(null, CurrencyCode.EUR));
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMajor(BigDecimal.ONE, null));
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMinor(100, null));
    }
}
