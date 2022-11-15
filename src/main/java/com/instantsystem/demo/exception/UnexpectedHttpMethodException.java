package com.instantsystem.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class UnexpectedHttpMethodException extends RuntimeException {
    public UnexpectedHttpMethodException(String message) {
        super(message);
    }
}
