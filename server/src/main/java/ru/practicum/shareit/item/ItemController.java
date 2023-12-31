package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Получен POST-запрос к эндпоинту: /items на создание новой вещи");
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER) Long ownerId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.debug("Получен PATCH-запрос к эндпоинту: /items на обновление вещи с id: {}", itemId);
        return itemService.update(ownerId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER) Long ownerId,
                               @PathVariable Long itemId) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение вещи с id: {}", itemId);
        return itemService.getItemById(ownerId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER) Long ownerId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение всех вещей владельца с id: {}", ownerId);
        return itemService.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET-запрос к эндпоинту: /items/search на получение вещи по тексту в описании и/или названии");
        return itemService.getItemsBySearchQuery(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER) Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.debug("Получен POST-запрос к эндпоинту: /items/{}/comment на создание отзыва", itemId);
        return itemService.createComment(userId, commentDto, itemId);
    }
}
