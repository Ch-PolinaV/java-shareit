package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Component
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, List<Comment> comments) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                toItemDto(booking.getItem(), comments),
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
