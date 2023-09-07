package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InputBookingDto implements Serializable {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
