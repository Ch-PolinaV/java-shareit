package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String OWNER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(OWNER) Long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.debug("Получен POST-запрос к эндпоинту: /items на создание новой вещи");
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(OWNER) Long ownerId,
                          @Valid @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.debug("Получен PATCH-запрос к эндпоинту: /items на обновление вещи с id: {}", itemId);
        return itemService.update(ownerId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение вещи с id: {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение всех вещей владельца с id: {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.debug("Получен GET-запрос к эндпоинту: /items/search на получение вещи по тексту в описании и/или названии");
        return itemService.getItemsBySearchQuery(text);
    }
}
