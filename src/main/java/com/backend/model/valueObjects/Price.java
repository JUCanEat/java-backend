package com.backend.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Embeddable
public class Price {

    private BigDecimal amount;
    private String currency;

    public Price(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be empty");
        }
        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    public Price add(Price other) {
        ensureSameCurrency(other);
        return new Price(this.amount.add(other.amount), this.currency);
    }

    public Price multiply(int quantity) {
        return new Price(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    private void ensureSameCurrency(Price other) {
        if (!this.currency.equalsIgnoreCase(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies");
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}