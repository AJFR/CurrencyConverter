package com.ajfr.currency.converter.dto;

import java.math.BigDecimal;

public record ExchangedCurrencyResponse(
        String currency, String exchangeCurrency, BigDecimal amount, BigDecimal exchangeRate,
        BigDecimal exchangedAmount
) {
}
