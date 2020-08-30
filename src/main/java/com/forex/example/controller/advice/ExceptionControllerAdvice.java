package com.forex.example.controller.advice;

import com.forex.example.exception.NegativeBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(NegativeBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(NegativeBalanceException exception) {
        log.error(exception.getMessage(), exception);
        return exception.getMessage();
    }
}
