package ru.practicum.shareit.booking;

import ru.practicum.shareit.exeption.UnsupportedStatusException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState toBookingState(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(" state : " + state);
        }
        return bookingState;
    }
}
