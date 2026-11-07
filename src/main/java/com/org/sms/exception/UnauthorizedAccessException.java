package com.org.sms.exception;

/** Thrown when a logged-in user tries to touch data they do not own / are not assigned to. */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
