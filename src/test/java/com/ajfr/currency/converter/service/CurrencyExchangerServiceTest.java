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

    private static final String CURRENCY = "CURRENCY";
    private static final String EXCHANGE_CURRENCY = "EXCHANGE_CURRENCY";
    private static final BigDecimal AMOUNT = BigDecimal.ONE;

    @Test
    public void exchangeCurrencyShouldReturn() throws FetchCurrencyDataServiceException {
        BigDecimal exchangeRate = BigDecimal.TEN;
        when(fetchCurrencyDataService.fetchCurrencyExchangeRate(CURRENCY, EXCHANGE_CURRENCY)).thenReturn(exchangeRate);

        ExchangedCurrencyResponse response = currencyExchangerService.exchangeCurrency(
                CURRENCY, EXCHANGE_CURRENCY, AMOUNT
        );

        ExchangedCurrencyResponse expectedResponse = new ExchangedCurrencyResponse(
                CURRENCY, EXCHANGE_CURRENCY, AMOUNT, exchangeRate, AMOUNT.multiply(exchangeRate)
        );
        assertEquals(expectedResponse, response);
    }

    @Test
    public void exchangeCurrencyShouldThrowFetchCurrencyDataServiceException()
            throws FetchCurrencyDataServiceException {
        when(fetchCurrencyDataService.fetchCurrencyExchangeRate(CURRENCY, EXCHANGE_CURRENCY))
                .thenThrow(FetchCurrencyDataServiceException.class);
        assertThrows(
                FetchCurrencyDataServiceException.class,
                () -> currencyExchangerService.exchangeCurrency(CURRENCY, EXCHANGE_CURRENCY, AMOUNT)
        );

    }

}