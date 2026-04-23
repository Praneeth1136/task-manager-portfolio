package com.praneeth.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown for unauthorized access to resources.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Assuming 403 Forbidden since the prompt mentions UnauthorizedException -> 403
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
