package ru.practicum.shareit.exceptions;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) {
        super(message);
    }
}