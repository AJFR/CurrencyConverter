package com.alex.currency.converter.service;

import com.alex.currency.converter.dto.CurrencyExchangeResponse;
import com.alex.currency.converter.exception.FetchCurrencyDataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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

    public Map<String, BigDecimal> fetchCurrencyExchangeRates(String currencyCode) throws FetchCurrencyDataServiceException {
        return getExchangeRates(currencyCode);
    }

    public BigDecimal fetchCurrencyExchangeRate(String currencyCode, String exchangeCode)
            throws FetchCurrencyDataServiceException {
        Map<String, BigDecimal> exchangeRates = getExchangeRates(currencyCode);
        if (!exchangeRates.containsKey(exchangeCode)) {
            throw new FetchCurrencyDataServiceException("No exchange data for " + currencyCode + "/" + exchangeCode);
        }
        return getExchangeRates(currencyCode).get(exchangeCode);
    }

    private Map<String, BigDecimal> getExchangeRates(String currencyCode) throws FetchCurrencyDataServiceException {
        try {
            URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                    .build(apiKey, currencyCode);
            CurrencyExchangeResponse response = restTemplate.exchange(
                    uri, GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
                    ).getBody();
            if (response == null) {
                throw new FetchCurrencyDataServiceException("Response is null.");
            }
            return response.conversion_rates();
        } catch (RestClientException e) {
            log.error("Error: {}", e.getMessage());
            throw new FetchCurrencyDataServiceException(e);
        }
    }
}
