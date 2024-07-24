package com.ajfr.currency.converter.service;

import com.ajfr.currency.converter.dto.ExchangedCurrencyResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CurrencyExchangerServiceTest {

    private final FetchCurrencyDataService fetchCurrencyDataService = mock(FetchCurrencyDataService.class);
    private final CurrencyExchangerService currencyExchangerService =
            new CurrencyExchangerService(fetchCurrencyDataService);

    private final String currency = "CURRENCY";
    private final String exchangeCurrency = "EXCHANGE_CURRENCY";
    private final BigDecimal amount = BigDecimal.ONE;

    @Test
    public void exchangeCurrencyShouldReturn() throws FetchCurrencyDataServiceException {
        BigDecimal exchangeRate = BigDecimal.TEN;
        when(fetchCurrencyDataService.fetchCurrencyExchangeRate(currency, exchangeCurrency)).thenReturn(exchangeRate);

        ExchangedCurrencyResponse response = currencyExchangerService.exchangeCurrency(
                currency, exchangeCurrency, amount
        );

        ExchangedCurrencyResponse expectedResponse = new ExchangedCurrencyResponse(
                currency, exchangeCurrency, amount, exchangeRate, amount.multiply(exchangeRate)
        );
        assertEquals(expectedResponse, response);
    }

    @Test
    public void exchangeCurrencyShouldThrowFetchCurrencyDataServiceException()
            throws FetchCurrencyDataServiceException {
        when(fetchCurrencyDataService.fetchCurrencyExchangeRate(currency, exchangeCurrency))
                .thenThrow(FetchCurrencyDataServiceException.class);
        assertThrows(
                FetchCurrencyDataServiceException.class,
                () -> currencyExchangerService.exchangeCurrency(currency, exchangeCurrency, amount)
        );

    }

}