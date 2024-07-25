package com.ajfr.currency.converter.controller;

import com.ajfr.currency.converter.dto.ExchangedCurrencyResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import com.ajfr.currency.converter.service.CurrencyExchangerService;
import com.ajfr.currency.converter.service.FetchCurrencyDataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConverterRestControllerTest {

    private final FetchCurrencyDataService fetchCurrencyDataService = mock(FetchCurrencyDataService.class);
    private final CurrencyExchangerService currencyExchangerService = mock(CurrencyExchangerService.class);
    private final CurrencyConverterRestController currencyConverterRestController = new CurrencyConverterRestController(
            fetchCurrencyDataService, currencyExchangerService
    );

    @Test
    void getCurrencyExchangeRatesShouldSucceed() throws FetchCurrencyDataServiceException {
        String currency = "GBP";
        Map<String, BigDecimal> exchangeRates = Map.of("USD", BigDecimal.TEN);
        when(fetchCurrencyDataService.fetchCurrencyExchangeRates(currency)).thenReturn(exchangeRates);
        ResponseEntity<Map<String, BigDecimal>> expectedResponse = ResponseEntity.ok(exchangeRates);
        assertEquals(expectedResponse, currencyConverterRestController.getCurrencyExchangeRates(currency));
    }

    @Test
    void getCurrencyExchangeRatesShouldThrow() throws FetchCurrencyDataServiceException {
        String currency = "GBP";
        when(fetchCurrencyDataService.fetchCurrencyExchangeRates(currency))
                .thenThrow(FetchCurrencyDataServiceException.class);
        assertThrows(FetchCurrencyDataServiceException.class, () ->
                currencyConverterRestController.getCurrencyExchangeRates(currency));
    }

    @Test
    void getCurrencyExchangeRatesUSDShouldSucceed() throws FetchCurrencyDataServiceException {
        String currency = "USD";
        Map<String, BigDecimal> exchangeRates = Map.of("GBP", BigDecimal.TEN);
        when(fetchCurrencyDataService.fetchCurrencyExchangeRates(currency)).thenReturn(exchangeRates);
        ResponseEntity<Map<String, BigDecimal>> expectedResponse = ResponseEntity.ok(exchangeRates);
        assertEquals(expectedResponse, currencyConverterRestController.getCurrencyExchangeRatesUSD());
    }

    @Test
    void getCurrencyExchangeRatesUSDShouldThrow() throws FetchCurrencyDataServiceException {
        String currency = "USD";
        when(fetchCurrencyDataService.fetchCurrencyExchangeRates(currency))
                .thenThrow(FetchCurrencyDataServiceException.class);
        assertThrows(
                FetchCurrencyDataServiceException.class, currencyConverterRestController::getCurrencyExchangeRatesUSD
        );
    }

    @Test
    void exchangeShouldSucceed() throws FetchCurrencyDataServiceException {
        String currency = "USD";
        String exchangeCurrency = "GBP";
        BigDecimal amount = BigDecimal.ONE;
        ExchangedCurrencyResponse expectedExchangeResponse = new ExchangedCurrencyResponse(
                currency, exchangeCurrency, amount, BigDecimal.TWO, BigDecimal.ZERO
        );
        when(currencyExchangerService.exchangeCurrency(currency, exchangeCurrency, amount))
                .thenReturn(expectedExchangeResponse);
        ResponseEntity<ExchangedCurrencyResponse> expectedResponse = ResponseEntity.ok(expectedExchangeResponse);
        assertEquals(expectedResponse, currencyConverterRestController.exchange(currency, exchangeCurrency, amount));
    }

    @Test
    void exchangeShouldThrow() throws FetchCurrencyDataServiceException {
        String currency = "USD";
        String exchangeCurrency = "GBP";
        BigDecimal amount = BigDecimal.ONE;
        when(currencyExchangerService.exchangeCurrency(currency, exchangeCurrency, amount))
                .thenThrow(FetchCurrencyDataServiceException.class);
        assertThrows(FetchCurrencyDataServiceException.class, () ->
                currencyConverterRestController.exchange(currency, exchangeCurrency, amount));
    }

}