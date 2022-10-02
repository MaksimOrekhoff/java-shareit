package ru.practicum.shareit.exceptions;

public class NotAvailableBooking extends RuntimeException {
    public NotAvailableBooking(String message) {
        super(message);
    }
}