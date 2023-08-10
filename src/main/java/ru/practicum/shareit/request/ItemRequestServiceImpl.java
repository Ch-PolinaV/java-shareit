package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
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

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + requestorId + " не найден!"));
        ItemRequest itemRequest = toItemRequest(itemRequestDto, requestor, created);

        itemRequestRepository.save(itemRequest);

        log.info("Добавлен новый запрос на вещь: {}", itemRequest);
        return toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + requestorId + " не найден!"));

        log.info("Получен список запросов пользователя с id: {}", requestorId);

        return itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestorId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        log.info("Получен список запросов других пользователей");

        return itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(userId, page)
                .map(ItemRequestMapper::toItemRequestDto)
                .getContent();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID=" + requestId + " не найден!"));

        log.info("Получен запрос с id: {}", requestId);

        return toItemRequestDto(itemRequest);
    }
}
