package com.alex.currency.converter.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CurrencyExchangeResponse(String result, String base_code, Map<String, BigDecimal> conversion_rates) {
}
