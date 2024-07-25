package com.ajfr.currency.converter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class FetchCurrencyDataServiceException extends Exception {

    private final HttpStatusCode httpStatusCode;

    public FetchCurrencyDataServiceException(String error, HttpStatusCode httpStatusCode) {
        super(error);
        this.httpStatusCode = httpStatusCode;
    }
}
