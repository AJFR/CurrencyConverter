package com.alex.currency.converter.exception;

public class FetchCurrencyDataServiceException extends Exception {

    public FetchCurrencyDataServiceException(Exception e) {
        super(e);
    }

    public FetchCurrencyDataServiceException(String error) {
        super(error);
    }
}
