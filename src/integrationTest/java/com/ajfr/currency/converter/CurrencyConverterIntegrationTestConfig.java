package com.ajfr.currency.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("integrationTest")
@TestPropertySource(locations = "classpath:application-integrationTest.properties")
public class CurrencyConverterIntegrationTestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }

}
