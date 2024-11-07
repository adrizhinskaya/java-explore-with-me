package ru.practicum.exception;

public class ValidationBadRequestException extends RuntimeException {
    public ValidationBadRequestException(String message) {
        super(message);
    }
}
