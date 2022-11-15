package com.instantsystem.demo.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BuildParkingException extends RuntimeException {
    public BuildParkingException(String message) {
        super(message);
    }
}
