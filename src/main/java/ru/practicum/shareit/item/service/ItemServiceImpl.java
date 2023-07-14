package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        if (userStorage.getUserById(ownerId) == null) {
            throw new NotFoundException("Пользователя с id: " + ownerId + " не существует");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getAvailable() == null) {
            throw new ValidationException("Введены некорректные данные при создании новой вещи");
        }
        return itemMapper.toItemDto(itemStorage.create(itemMapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto update(Long ownerId, ItemDto itemDto, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);

        if (!oldItem.getOwner().equals(ownerId)) {
            throw new NotFoundException("Данная вещь принадлежит другому пользователю");
        }

        return itemMapper.toItemDto(itemStorage.update(itemMapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long id) {
        return itemStorage.getItemsByOwner(id).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        return itemStorage.getItemsBySearchQuery(text.toLowerCase()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
