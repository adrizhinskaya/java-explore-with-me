package ru.practicum.exception;

public class ConstraintConflictException extends RuntimeException {
    public ConstraintConflictException(String message) {
        super(message);
    }
}
