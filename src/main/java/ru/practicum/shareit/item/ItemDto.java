package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.validation.CreateValidationGroup;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto implements Serializable {
    private Long id;
    @NotEmpty(groups = CreateValidationGroup.class)
    private String name;
    @NotEmpty(groups = CreateValidationGroup.class)
    private String description;
    @NotNull(groups = CreateValidationGroup.class)
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}
