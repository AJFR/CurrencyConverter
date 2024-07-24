package com.ajfr.currency.converter.exception.handler;

import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CurrencyConverterExceptionHandler {

    @ExceptionHandler(FetchCurrencyDataServiceException.class)
    public ResponseEntity handleFetchCurrencyDataServiceException(FetchCurrencyDataServiceException e) {
        return ResponseEntity.internalServerError().build();
    }

}
