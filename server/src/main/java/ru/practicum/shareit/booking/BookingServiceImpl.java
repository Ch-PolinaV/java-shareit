package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.BookingState.toBookingState;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto create(Long bookerId, InputBookingDto inputBooking) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + bookerId + " не найден!"));
        Item item = itemRepository.findById(inputBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + inputBooking.getItemId() + " не найдена!"));
        Booking booking = toBooking(inputBooking, booker, item);

        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Вещь не может быть забронирована ее владельцем");
        }
        if (inputBooking.getStart() == null || inputBooking.getEnd() == null) {
            throw new ValidationException("Время начала и окончания должны быть указаны");
        }
        if (inputBooking.getStart().isAfter(inputBooking.getEnd()) || inputBooking.getStart().isEqual(inputBooking.getEnd())) {
            throw new ValidationException("Некорректно указано время: дата окончания должна быть позже даты начала");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с ID=" + item.getId() + " в данный момент не доступна для бронирования");
        }

        bookingRepository.save(booking);
        log.info("Добавлено новое бронирование: {}", booking);

        return toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto update(Long userId, Boolean approved, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + booking.getItem().getId() + " не найдена!"));

        if (item.getOwner().getId().equals(userId)) {
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new ValidationException("Невозможно изменить статус");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
                log.info("Бронирование с id: {} подтверждено", bookingId);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
                log.info("Бронирование с id: {} отклонено", bookingId);
            }
        } else {
            throw new NotFoundException("Подтверждение или отклонение запроса на бронирование может быть выполнено только владельцем вещи");
        }

        bookingRepository.save(booking);

        return toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + booking.getItem().getId() + " не найдена!"));

        if (item.getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return toBookingDto(booking);
        } else {
            throw new NotFoundException("Получение данных о бронировании может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование");
        }
    }

    @Override
    public List<BookingDto> getAllUserBookings(Long userId, String stateStr, Integer from, Integer size) {
        BookingState state = toBookingState(stateStr);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        PageRequest page = PageRequest.of(from > 0 ? from / size : from, size, sort);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден!"));
        Page<Booking> bookings = null;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, page);
                break;
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, page);
                break;
        }

        return bookings.map(BookingMapper::toBookingDto).getContent();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String stateStr, Integer from, Integer size) {
        BookingState state = toBookingState(stateStr);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        PageRequest page = PageRequest.of(from > 0 ? from / size : from, size, sort);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + ownerId + " не найден!"));
        Page<Booking> bookings = null;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndAfter(ownerId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, page);
                break;
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(ownerId, page);
                break;
        }

        return bookings.map(BookingMapper::toBookingDto).getContent();
    }
}
