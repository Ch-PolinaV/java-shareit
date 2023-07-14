package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, ItemDto itemDto, Long itemId);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwner(Long id);

    List<ItemDto> getItemsBySearchQuery(String text);
}
