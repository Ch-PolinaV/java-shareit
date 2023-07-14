package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.AlreadyExistsException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1L;

    @Override
    public Item create(Item item) {
        if (items.containsKey(item.getId())) {
            log.info("Вещь с id: {} уже существует", item.getId());
            throw new AlreadyExistsException("Вещь с указанным id уже была добавлена ранее");
        }
        log.info("Добавлена новая вещь: {}", item);
        item.setId(createId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        log.info("Вещь с id: {} обновлена", item.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long id) {
        if (!items.containsKey(id)) {
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByOwner(Long id) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private long createId() {
        return id++;
    }
}
