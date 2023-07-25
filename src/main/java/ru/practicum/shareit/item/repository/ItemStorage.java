package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item getItemById(Long id);

    List<Item> getItemsByOwner(Long id);

    List<Item> getItemsBySearchQuery(String text);
}
