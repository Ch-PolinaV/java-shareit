package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto implements Serializable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemForBookingDto item;
    private UserDto booker;
    private BookingStatus status;
}
