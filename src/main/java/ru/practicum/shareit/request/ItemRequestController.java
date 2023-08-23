package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER) Long requestorId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Получен POST-запрос к эндпоинту: /requests на создание запроса от пользователя с id: {}", requestorId);
        return itemRequestService.create(itemRequestDto, requestorId, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(USER) Long requestorId) {
        log.debug("Получен GET-запрос к эндпоинту: /requests на получение списка всех запросов пользователя с id: {}", requestorId);
        return itemRequestService.getOwnItemRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER) Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET-запрос к эндпоинту: /requests/all на получение списка всех запросов от пользователя с id: {}", userId);
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(USER) Long userId,
                                             @PathVariable Long requestId) {
        log.debug("Получен GET-запрос к эндпоинту: /requests/{requestId} на получение запроса с id: {}", requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
