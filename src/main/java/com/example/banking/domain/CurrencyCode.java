package com.example.banking.domain;

public enum CurrencyCode {
    EUR(2);

    private final int fractionDigits;

    CurrencyCode(int fractionDigits) {
        if (fractionDigits < 0) {
            throw new IllegalArgumentException("Fraction digits cannot be negative");
        }

        this.fractionDigits = fractionDigits;
    }

    public int fractionDigits() {
        return fractionDigits;
    }
}
