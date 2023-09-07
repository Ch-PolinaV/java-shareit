package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + requestorId + " не найден!"));
        ItemRequest itemRequest = toItemRequest(itemRequestDto, requestor, created);

        itemRequestRepository.save(itemRequest);

        log.info("Добавлен новый запрос на вещь: {}", itemRequest);
        return toItemRequestDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + requestorId + " не найден!"));

        log.info("Получен список запросов пользователя с id: {}", requestorId);

        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(ItemRequestMapper -> toItemRequestDto(ItemRequestMapper, getItemsByRequest(ItemRequestMapper.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : from, size);

        log.info("Получен список запросов других пользователей");

        return itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page)
                .map(ItemRequestMapper -> toItemRequestDto(ItemRequestMapper, getItemsByRequest(ItemRequestMapper.getId())))
                .getContent();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID=" + requestId + " не найден!"));

        log.info("Получен запрос с id: {}", requestId);

        return toItemRequestDto(itemRequest, getItemsByRequest(requestId));
    }

    List<ItemForItemRequestDto> getItemsByRequest(Long requestId) {
        itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден!"));

        return itemRepository.findByRequest_IdOrderById(requestId).stream()
                .map(ItemMapper::toItemForItemRequestDto)
                .collect(Collectors.toList());
    }
}
