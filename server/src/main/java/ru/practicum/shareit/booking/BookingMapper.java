package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.item.ItemMapper.toItemForBookingDto;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                toItemForBookingDto(booking.getItem()),
                toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }

    public static Booking toBooking(InputBookingDto bookingDto, User user, Item item) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }
}
