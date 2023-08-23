package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created);

    List<ItemRequestDto> getOwnItemRequests(Long requestorId);

    List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
