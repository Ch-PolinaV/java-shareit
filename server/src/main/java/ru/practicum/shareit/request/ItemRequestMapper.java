package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemForItemRequestDto> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                items
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, LocalDateTime created) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                created
        );
    }
}
