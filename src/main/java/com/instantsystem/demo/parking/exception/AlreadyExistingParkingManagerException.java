package com.instantsystem.demo.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistingParkingManagerException extends RuntimeException {
    public AlreadyExistingParkingManagerException(String city) {
        super(String.format("Parking manager already exists for city %s", city));
    }
}
