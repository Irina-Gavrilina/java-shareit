package ru.practicum.shareit.exception;

public class UnavailableBookingException extends RuntimeException {
    public UnavailableBookingException(String message) {
        super(message);
    }
}