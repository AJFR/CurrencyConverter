package com.ajfr.currency.converter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy
@Profile("!integrationTest")
public class CurrencyConverterConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
