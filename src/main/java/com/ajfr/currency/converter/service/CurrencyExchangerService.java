package com.ajfr.currency.converter.service;

import com.ajfr.currency.converter.dto.ExchangedCurrencyResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyExchangerService {

private final FetchCurrencyDataService fetchCurrencyDataService;

    public CurrencyExchangerService(FetchCurrencyDataService fetchCurrencyDataService) {
        this.fetchCurrencyDataService = fetchCurrencyDataService;
    }

    public ExchangedCurrencyResponse exchangeCurrency(String currency, String exchangeCurrency, BigDecimal amount)
            throws FetchCurrencyDataServiceException {
        BigDecimal exchangeRate = fetchCurrencyDataService.fetchCurrencyExchangeRate(currency, exchangeCurrency);
        BigDecimal exchangedAmount = amount.multiply(exchangeRate);
        return new ExchangedCurrencyResponse(currency, exchangeCurrency, amount, exchangeRate, exchangedAmount);
    }
}
