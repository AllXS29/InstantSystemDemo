package com.instantsystem.demo.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonExistingParkingManagerException extends RuntimeException {
    public NonExistingParkingManagerException(String city) {
        super(String.format("No parking manager found for city %s", city));
    }
}
