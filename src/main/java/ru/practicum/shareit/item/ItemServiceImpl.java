package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingForItemDto;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + ownerId + " не найден!"));
        Item item;

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден!"));
            item = itemRepository.save(toItem(itemDto, user, request));
        } else {
            item = itemRepository.save(toItem(itemDto, user, null));
        }

        log.info("Добавлена новая вещь: {}", itemDto);

        List<Comment> comments = commentRepository.findByItemId(item.getId());

        return toItemDto(item, comments);
    }

    @Transactional
    @Override
    public ItemDto update(Long ownerId, ItemDto itemDto, Long itemId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + ownerId + " не найден!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Данная вещь принадлежит другому пользователю");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        ItemDto updateItemDto = toItemDto(item, comments);
        itemRepository.save(toItem(updateItemDto, user, item.getRequest()));

        log.info("Вещь с id: {} обновлена", item.getId());

        return toItemDto(item, comments);
    }

    @Override
    public ItemDto getItemById(Long ownerId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        ItemDto itemDto = toItemDto(item, comments);

        if (item.getOwner().getId().equals(ownerId)) {
            Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now());
            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);

            if (lastBooking != null) {
                itemDto.setLastBooking(toBookingForItemDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(toBookingForItemDto(nextBooking));
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : from, size);

        List<Item> items = itemRepository.findByOwner_Id(ownerId, page).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            ItemDto itemDto = toItemDto(item, comments);

            Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

            if (lastBooking != null) {
                itemDto.setLastBooking(toBookingForItemDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(toBookingForItemDto(nextBooking));
            }

            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : from, size);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        Page<Item> items = itemRepository.findBySearchQuery(text.toLowerCase(), page);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            ItemDto itemDto = toItemDto(item, comments);
            itemDtos.add(itemDto);
        }

        return itemDtos;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        Comment comment;

        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (booking != null) {
            comment = toComment(commentDto, item, user);
        } else {
            throw new ValidationException("Отзывы могут оставлять только пользователи, которые брали вещь в аренду");
        }
        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemForItemRequestDto> getItemsByRequest(Long requestId) {
        itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден!"));

        return itemRepository.findByRequest_IdOrderById(requestId).stream()
                .map(ItemMapper::toItemForItemRequestDto)
                .collect(Collectors.toList());
    }
}
