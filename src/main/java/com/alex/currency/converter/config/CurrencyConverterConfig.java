package com.alex.currency.converter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy
public class CurrencyConverterConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
