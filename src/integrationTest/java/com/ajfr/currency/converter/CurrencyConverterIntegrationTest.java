package com.ajfr.currency.converter;

import com.ajfr.currency.converter.controller.CurrencyConverterRestController;
import com.ajfr.currency.converter.dto.CurrencyExchangeResponse;
import com.ajfr.currency.converter.dto.ErrorResponse;
import com.ajfr.currency.converter.dto.ExchangedCurrencyResponse;
import com.ajfr.currency.converter.exception.handler.CurrencyConverterExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integrationTest")
class CurrencyConverterIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CurrencyConverterRestController currencyConverterRestController;
    @Autowired
    private CurrencyConverterExceptionHandler currencyConverterExceptionHandler;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private MockRestServiceServer mockServer;
    @Value("${exchange.rate.url.template}")
    private String urlTemplate;
    @Value("${exchange.rate.api.key}")
    private String apiKey;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(currencyConverterRestController)
                .setControllerAdvice(currencyConverterExceptionHandler)
                .build();
        mockServer = MockRestServiceServer.createServer(restTemplate);;
    }

    @Test
    public void testGetCurrencyExchangeRateUSD() throws Exception {
        Map<String, BigDecimal> exchangeRates = Map.of("GBP", BigDecimal.TEN);
        mockExchangeRateAPICall("USD", exchangeRates);

        Map<String, BigDecimal> response = objectMapper.readValue(
                mockMvc.perform(get("/getCurrencyExchangeRateUSD").accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<HashMap<String, BigDecimal>>() {}
        );

        assertEquals(exchangeRates, response);
    }

    @Test
    public void testGetCurrencyExchangeRate() throws Exception {
        String currency = "GBP";
        Map<String, BigDecimal> exchangeRates = Map.of("USD", BigDecimal.TEN);
        mockExchangeRateAPICall(currency, exchangeRates);

        Map<String, BigDecimal> response = objectMapper.readValue(
                mockMvc.perform(
                        get("/getCurrencyExchangeRates/" + currency).accept("application/json")
                        ).andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), new TypeReference<HashMap<String, BigDecimal>>() {}
        );

        assertEquals(exchangeRates, response);
    }

    @Test

    public void testGetCurrencyExchangeRateWithNonValidCurrency() throws Exception {
        String invalidCurrency = "G1";
        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/getCurrencyExchangeRates/" + invalidCurrency)
                                        .accept("application/json")
                        ).andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );

        assertTrue(response.errorMessage().contains("getCurrencyExchangeRates.currency: Currency must match XXX. I.e. GBP."));
    }

    @Test
    public void testGetCurrencyExchangeRatesWhenCurrencyIsNotFound() throws Exception {
        String currency = "GBP";
        mockExchangeRateAPICallCurrencyNotFound(currency);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/getCurrencyExchangeRates/" + currency)
                                        .accept("application/json")
                        ).andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );

        assertTrue(response.errorMessage().contains("Unable to find currency: " + currency + "."));
    }

    @Test
    public void testGetCurrencyExchangeRatesWhenResponseIsNull() throws Exception {
        String currency = "GBP";
        mockExchangeRateAPICallReturnNull(currency);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/getCurrencyExchangeRates/" + currency)
                                        .accept("application/json")
                        ).andExpect(status().isInternalServerError())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );

        assertTrue(response.errorMessage().contains("Response is null for currency: " + currency + "."));
    }

    @Test
    public void testExchange() throws Exception {
        String currency = "GBP";
        String exchangeCurrency = "USD";
        BigDecimal amount = BigDecimal.TEN;
        BigDecimal exchangeRate = BigDecimal.TWO;
        Map<String, BigDecimal> exchangeRates = Map.of(exchangeCurrency, exchangeRate);
        mockExchangeRateAPICall(currency, exchangeRates);

        ExchangedCurrencyResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/exchange/" + currency + "/" + exchangeCurrency + "/ " + amount)
                                        .accept("application/json")
                        ).andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ExchangedCurrencyResponse.class
        );

        ExchangedCurrencyResponse expectedResponse = new ExchangedCurrencyResponse(
                currency, exchangeCurrency, amount, exchangeRate, amount.multiply(exchangeRate)
        );
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testExchangeWhenArgsAreNotValid() throws Exception {
        String invalidCurrency = "G1";
        String exchangeCurrency = "U1";
        BigDecimal amount = BigDecimal.valueOf(-1);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/exchange/" + invalidCurrency + "/" + exchangeCurrency + "/ " + amount)
                                        .accept("application/json")
                        ).andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );

        String errorMessage = response.errorMessage();
        assertTrue(errorMessage.contains("exchange.currency: Currency must match XXX. I.e. GBP."));
        assertTrue(errorMessage.contains(
                        "exchange.exchangeCurrency: Exchange currency must match XXX. I.e. GBP."
                ));
        assertTrue(errorMessage.contains(
                "exchange.amount: Amount must be sent as positive non-zero integer. I.e. 1000"
        ));
    }

    @Test
    public void testExchangeWhenResponseDoesNotContainExchangeCurrency() throws Exception {
        String currency = "GBP";
        String exchangeCurrency = "USD";
        BigDecimal amount = BigDecimal.TEN;
        BigDecimal exchangeRate = BigDecimal.TWO;
        Map<String, BigDecimal> exchangeRates = Map.of("not exchange currency", exchangeRate);
        mockExchangeRateAPICall(currency, exchangeRates);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/exchange/" + currency + "/" + exchangeCurrency + "/ " + amount)
                                        .accept("application/json")
                        ).andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );

        assertTrue(response.errorMessage().contains("No exchange data for " + currency + "/" + exchangeCurrency + "."));
    }

    @Test
    public void testExchangeWhenCurrencyNotFound() throws Exception {
        String currency = "GBP";
        String exchangeCurrency = "USD";
        BigDecimal amount = BigDecimal.TEN;
        mockExchangeRateAPICallCurrencyNotFound(currency);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/exchange/" + currency + "/" + exchangeCurrency + "/ " + amount)
                                        .accept("application/json")
                        ).andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );


        assertTrue(response.errorMessage().contains("Unable to find currency: " + currency + "."));
    }

    @Test
    public void testExchangeWhenCurrencyResponseIsNull() throws Exception {
        String currency = "GBP";
        String exchangeCurrency = "USD";
        BigDecimal amount = BigDecimal.TEN;
        mockExchangeRateAPICallReturnNull(currency);

        ErrorResponse response = objectMapper.readValue(
                mockMvc.perform(
                                get("/exchange/" + currency + "/" + exchangeCurrency + "/ " + amount)
                                        .accept("application/json")
                        ).andExpect(status().isInternalServerError())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ErrorResponse.class
        );


        assertTrue(response.errorMessage().contains("Response is null for currency: " + currency + "."));
    }

    private void mockExchangeRateAPICall(String currency, Map<String, BigDecimal> exchangeRates)
            throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                .build(apiKey, currency);
        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new CurrencyExchangeResponse(
                                "result", currency, exchangeRates
                        ))));
    }

    private void mockExchangeRateAPICallReturnNull(String currency) {
        URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                .build(apiKey, currency);
        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(EMPTY_STRING));
    }

    private void mockExchangeRateAPICallCurrencyNotFound(String currency) {
        URI uri = UriComponentsBuilder.fromUriString(urlTemplate)
                .build(apiKey, currency);
        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andRespond((_) -> {
                    throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
                });
    }

}
