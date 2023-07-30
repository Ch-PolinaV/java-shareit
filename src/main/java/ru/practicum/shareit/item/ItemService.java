package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long ownerId, ItemDto itemDto, Long itemId);

    ItemDto getItemById(Long ownerId, Long itemId);

    List<ItemDto> getItemsByOwner(Long id);

    List<ItemDto> getItemsBySearchQuery(String text);

    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);
}