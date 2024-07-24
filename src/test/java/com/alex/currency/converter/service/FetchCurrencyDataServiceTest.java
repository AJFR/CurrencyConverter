package com.alex.currency.converter.service;

import com.alex.currency.converter.dto.CurrencyExchangeResponse;
import com.alex.currency.converter.exception.FetchCurrencyDataServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

class FetchCurrencyDataServiceTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final String urlTemplate = "https://v6.exchangerate-api.com/v6/{apiKey}/latest/{currency}";
    private final String apiKey = "xyz123";
    private final FetchCurrencyDataService fetchCurrencyDataService =
            new FetchCurrencyDataService(restTemplate, urlTemplate, apiKey);

    private final String currency = "CURRENCY";
    private final String exchangeCurrency = "EXCHANGE_CURRENCY";
    private final BigDecimal exchangeRate = BigDecimal.ONE;
    private final Map<String, BigDecimal> exchangeRates = Map.of(
            exchangeCurrency, exchangeRate
    );
    private final URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
            .build(apiKey, currency);

    @Test
    public void fetchCurrencyExchangeRatesShouldReturn() throws FetchCurrencyDataServiceException {
        mockRestTemplateToReturn();

        assertEquals(exchangeRates, fetchCurrencyDataService.fetchCurrencyExchangeRates(currency));
    }

    @Test
    public void fetchCurrencyExchangeRatesShouldThrowWhenResponseIsNull() {
        when(restTemplate.exchange(
                uri, HttpMethod.GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
        )).thenReturn(new ResponseEntity<>(
               null, HttpStatus.OK)
        );

        assertThrows(FetchCurrencyDataServiceException.class, () ->
                fetchCurrencyDataService.fetchCurrencyExchangeRates(currency)
        );
    }

    @Test
    public void fetchCurrencyExchangeRatesShouldThrowWhenResponseIsNotOK() {
        when(restTemplate.exchange(
                uri, HttpMethod.GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
        )).thenThrow(RestClientException.class);

        assertThrows(FetchCurrencyDataServiceException.class, () ->
                fetchCurrencyDataService.fetchCurrencyExchangeRates(currency)
        );
    }

    @Test
    public void fetchCurrencyExchangeRateShouldReturn() throws FetchCurrencyDataServiceException {
        mockRestTemplateToReturn();

        assertEquals(exchangeRate, fetchCurrencyDataService.fetchCurrencyExchangeRate(currency, exchangeCurrency));
    }

    @Test
    public void fetchCurrencyExchangeRateShouldThrowWhenExchangeRatesDoNotContainExchangeCurrency() {
        mockRestTemplateToReturn();

        assertThrows(FetchCurrencyDataServiceException.class,
                () -> fetchCurrencyDataService.fetchCurrencyExchangeRate(currency, "error")
        );
    }

    private void mockRestTemplateToReturn() {
        when(restTemplate.exchange(
                uri, HttpMethod.GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
        )).thenReturn(new ResponseEntity<>(
                new CurrencyExchangeResponse("success", currency, exchangeRates), HttpStatus.OK
                ));
    }

}