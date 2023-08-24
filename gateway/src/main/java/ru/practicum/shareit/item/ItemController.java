package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.CreateValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated(CreateValidationGroup.class) @RequestBody ItemDto itemDto) {
        log.debug("Получен POST-запрос к эндпоинту: /items на создание новой вещи");
        return itemClient.create(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId) {
        log.debug("Получен PATCH-запрос к эндпоинту: /items на обновление вещи с id: {}", itemId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение вещи с id: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен GET-запрос к эндпоинту: /items на получение всех вещей владельца с id: {}", userId);
        return itemClient.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearchQuery(@RequestParam String text,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен GET-запрос к эндпоинту: /items/search на получение вещи по тексту в описании и/или названии");
        return itemClient.getItemsBySearchQuery(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid CommentDto commentDto,
                                           @PathVariable Long itemId) {
        log.debug("Получен POST-запрос к эндпоинту: /items/{}/comment на создание отзыва", itemId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}
