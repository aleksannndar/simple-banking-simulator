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
                .isThrownBy(() -> Money.ofMajor(new BigDecimal("12.345"), CurrencyCode.EUR));
    }

    @Test
    void rejectsAmountsOutsideLongRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Money.ofMajor(
                        new BigDecimal("92233720368547758.08"),
                        CurrencyCode.EUR
                ));
    }

    @Test
    void rejectsNullMajorUnitValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMajor(null, CurrencyCode.EUR));
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMajor(BigDecimal.ONE, null));
    }

    @Test
    void rejectsNullMinorUnitCurrency() {
        assertThatNullPointerException()
                .isThrownBy(() -> Money.ofMinor(100, null));
    }

    @Test
    void createsZeroForCurrency() {
        assertThat(Money.zero(CurrencyCode.EUR))
                .isEqualTo(Money.ofMinor(0, CurrencyCode.EUR));
    }

    @Test
    void addsAndSubtractsMoneyWithoutMutatingOriginalValues() {
        Money original = Money.ofMinor(1_000, CurrencyCode.EUR);
        Money other = Money.ofMinor(250, CurrencyCode.EUR);

        assertThat(original.add(other)).isEqualTo(Money.ofMinor(1_250, CurrencyCode.EUR));
        assertThat(original.subtract(other)).isEqualTo(Money.ofMinor(750, CurrencyCode.EUR));
        assertThat(original).isEqualTo(Money.ofMinor(1_000, CurrencyCode.EUR));
    }

}
