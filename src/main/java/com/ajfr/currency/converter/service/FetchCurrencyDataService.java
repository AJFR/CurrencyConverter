package com.ajfr.currency.converter.service;

import com.ajfr.currency.converter.dto.CurrencyExchangeResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Service
@Slf4j
public class FetchCurrencyDataService {

    private final RestTemplate restTemplate;
    private final String urlTemplate;
    private final String apiKey;

    @Autowired
    public FetchCurrencyDataService(
            RestTemplate restTemplate, @Value("${exchange.rate.url.template}") String urlTemplate,
            @Value("${exchange.rate.api.key}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.urlTemplate = urlTemplate;
        this.apiKey = apiKey;
    }

    public Map<String, BigDecimal> fetchCurrencyExchangeRates(String currencyCode)
            throws FetchCurrencyDataServiceException {
        return getExchangeRates(currencyCode);
    }

    public BigDecimal fetchCurrencyExchangeRate(String currencyCode, String exchangeCode)
            throws FetchCurrencyDataServiceException {
        Map<String, BigDecimal> exchangeRates = getExchangeRates(currencyCode);
        if (!exchangeRates.containsKey(exchangeCode)) {
            throw new FetchCurrencyDataServiceException(
                    "No exchange data for " + currencyCode + "/" + exchangeCode + ".", HttpStatus.NOT_FOUND
            );
        }
        return exchangeRates.get(exchangeCode);
    }

    private Map<String, BigDecimal> getExchangeRates(String currencyCode) throws FetchCurrencyDataServiceException {
        try {
            URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                    .build(apiKey, currencyCode);
                ResponseEntity<CurrencyExchangeResponse> response = restTemplate.exchange(
                        uri, GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
                        );
            if (response.getBody() == null) {
                throw new FetchCurrencyDataServiceException(
                        "Response is null for currency: " + currencyCode + ".", HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            return response.getBody()
                    .conversion_rates();
        } catch (HttpStatusCodeException e) {
            log.error("Error: {}.", e.getMessage());
            throw new FetchCurrencyDataServiceException(
                    "Unable to find currency: " + currencyCode + ".", e.getStatusCode()
            );
        }
    }
}
