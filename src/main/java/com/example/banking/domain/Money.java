package com.example.banking.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Money {
    private final long minorUnits;
    private final CurrencyCode currency;

    private Money(long minorUnits, CurrencyCode currency) {
        this.minorUnits = minorUnits;
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
    }

    public static Money ofMajor(BigDecimal majorUnits, CurrencyCode currency) {
        Objects.requireNonNull(majorUnits, "Major units cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        try {
            long minorUnits = majorUnits
                    .movePointRight(currency.fractionDigits())
                    .longValueExact();
            return new Money(minorUnits, currency);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Amount cannot be represented exactly in minor units: " + majorUnits + " " + currency,
                    exception
            );
        }
    }

    public static Money ofMinor(long minorUnits, CurrencyCode currency) {
        return new Money(minorUnits, currency);
    }

    public static Money zero(CurrencyCode currency) {
        return ofMinor(0, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return ofMinor(Math.addExact(minorUnits, other.minorUnits), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return ofMinor(Math.subtractExact(minorUnits, other.minorUnits), currency);
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return minorUnits > other.minorUnits;
    }

    public boolean isPositive() {
        return minorUnits > 0;
    }

    public boolean isNegative() {
        return minorUnits < 0;
    }

    public long minorUnits() {
        return minorUnits;
    }

    public CurrencyCode currency() {
        return currency;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money cannot be null");
        if (currency != other.currency) {
            throw new IllegalArgumentException(
                    "Currencies must match: " + currency + " and " + other.currency
            );
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Money money)) {
            return false;
        }
        return minorUnits == money.minorUnits && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minorUnits, currency);
    }

    @Override
    public String toString() {
        return "Money[minorUnits=" + minorUnits + ", currency=" + currency + ']';
    }
}
