package com.ajfr.currency.converter;

import com.ajfr.currency.converter.controller.CurrencyConverterRestController;
import com.ajfr.currency.converter.dto.CurrencyExchangeResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=integrationTest"
)
class CurrencyConverterIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CurrencyConverterRestController currencyConverterRestController;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    @Value("${exchange.rate.url.template}")
    private String urlTemplate;
    @Value("${exchange.rate.api.key}")
    private String apiKey;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(currencyConverterRestController).build();
    }

    @Test
    public void testGetCurrencyExchangeRateUSD() throws Exception {
        Map<String, BigDecimal> exchangeRates = Map.of("GBP", BigDecimal.TEN);
        mockExchangeRateAPICall("USD", exchangeRates);

        Map<String, BigDecimal> response = getResponse(
                mockMvc.perform(get("/getCurrencyExchangeRateUSD").accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<HashMap<String, BigDecimal>>() {});

        assertEquals(exchangeRates, response);
    }

    @Test
    public void testGetCurrencyExchangeRate() throws Exception {
        Map<String, BigDecimal> exchangeRates = Map.of("USD", BigDecimal.TEN);
        mockExchangeRateAPICall("GBP", exchangeRates);

        Map<String, BigDecimal> response = getResponse(
                mockMvc.perform(get("/getCurrencyExchangeRates/GBP").accept("application/json"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), new TypeReference<HashMap<String, BigDecimal>>() {});

        assertEquals(exchangeRates, response);
    }

    private <M> M getResponse(String response, TypeReference<M> responseType) throws Exception {
        return objectMapper.readValue(response, responseType);
    }

    private void mockExchangeRateAPICall(String currency, Map<String, BigDecimal> exchangeRates) {
        URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                .build(apiKey, currency);
        when(restTemplate.exchange(
                uri, GET, new RequestEntity<Void>(GET, uri), CurrencyExchangeResponse.class
        )).thenReturn(ResponseEntity.ok(new CurrencyExchangeResponse("result", currency, exchangeRates)));
    }

}
