package com.ajfr.currency.converter.controller;

import com.ajfr.currency.converter.dto.ExchangedCurrencyResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import com.ajfr.currency.converter.service.CurrencyExchangerService;
import com.ajfr.currency.converter.service.FetchCurrencyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class CurrencyConverterRestController {

    private final FetchCurrencyDataService fetchCurrencyDataService;
    private final CurrencyExchangerService currencyExchangerService;
    private static final String USD = "USD";

    @Autowired
    public CurrencyConverterRestController(
            FetchCurrencyDataService fetchCurrencyDataService, CurrencyExchangerService currencyExchangerService
    ) {
     this.fetchCurrencyDataService = fetchCurrencyDataService;
     this.currencyExchangerService  = currencyExchangerService;
    }

    @GetMapping(path ="/getCurrencyExchangeRates/{currency}", produces = "application/json")
    public ResponseEntity<Map<String, BigDecimal>> getCurrencyExchangeRates(@PathVariable String currency)
            throws FetchCurrencyDataServiceException {
        return ResponseEntity.ok(fetchCurrencyDataService.fetchCurrencyExchangeRates(currency));
    }

    @GetMapping(path ="/getCurrencyExchangeRateUSD", produces = "application/json")
    public ResponseEntity<Map<String, BigDecimal>> getCurrencyExchange()
            throws FetchCurrencyDataServiceException {
        return ResponseEntity.ok(fetchCurrencyDataService.fetchCurrencyExchangeRates(USD));
    }

    @GetMapping(path = "/exchange/{currency}/{exchangeCurrency}/{amount}")
    public ResponseEntity<ExchangedCurrencyResponse> exchange(
            @PathVariable String currency, @PathVariable String exchangeCurrency, @PathVariable BigDecimal amount
    ) throws FetchCurrencyDataServiceException {
        return ResponseEntity.ok(currencyExchangerService.exchangeCurrency(currency, exchangeCurrency, amount));
    }

}
