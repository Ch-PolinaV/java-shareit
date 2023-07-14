package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        userStorage.getUserById(ownerId);
        return toItemDto(itemStorage.create(toItem(itemDto, userStorage.getUserById(ownerId))));
    }

    @Override
    public ItemDto update(Long ownerId, ItemDto itemDto, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);

        if (!oldItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Данная вещь принадлежит другому пользователю");
        }

        return toItemDto(itemStorage.update(toItem(itemDto, userStorage.getUserById(ownerId))));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long id) {
        return itemStorage.getItemsByOwner(id).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        return itemStorage.getItemsBySearchQuery(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
