package com.ajfr.currency.converter.exception.handler;

import com.ajfr.currency.converter.dto.ErrorResponse;
import com.ajfr.currency.converter.exception.FetchCurrencyDataServiceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ControllerAdvice
public class CurrencyConverterExceptionHandler {

    @ExceptionHandler(FetchCurrencyDataServiceException.class)
    public ResponseEntity handleFetchCurrencyDataServiceException(FetchCurrencyDataServiceException e) {
        return new ResponseEntity(new ErrorResponse(e.getMessage()), e.getHttpStatusCode());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse constraintViolationException(ConstraintViolationException e) throws IOException {
        return new ErrorResponse(e.getMessage());
    }

}
