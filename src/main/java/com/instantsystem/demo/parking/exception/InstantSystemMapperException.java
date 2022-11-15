package com.instantsystem.demo.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InstantSystemMapperException extends RuntimeException {
    public InstantSystemMapperException(String message) {
        super(message);
    }
}
