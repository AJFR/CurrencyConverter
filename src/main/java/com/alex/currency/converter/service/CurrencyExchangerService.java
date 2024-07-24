package com.alex.currency.converter.service;

import com.alex.currency.converter.dto.ExchangedCurrencyResponse;
import com.alex.currency.converter.exception.FetchCurrencyDataServiceException;
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
