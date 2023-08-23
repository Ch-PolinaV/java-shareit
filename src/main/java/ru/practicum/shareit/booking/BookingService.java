package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long bookerId, InputBookingDto inputBookingDto);

    BookingDto update(Long userId, Boolean approved, Long bookingId);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size);
}
