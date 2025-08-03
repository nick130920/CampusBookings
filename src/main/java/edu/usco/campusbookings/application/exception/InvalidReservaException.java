package edu.usco.campusbookings.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReservaException extends RuntimeException {
    public InvalidReservaException(String message) {
        super(message);
    }
}
