package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(USER) Long bookerId,
                             @Valid @RequestBody InputBookingDto inputBookingDto) {
        log.debug("Получен POST-запрос к эндпоинту: /bookings на создание бронирования от пользователя с id: {}", bookerId);
        return bookingService.create(bookerId, inputBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(USER) Long userId,
                          @RequestParam Boolean approved,
                          @PathVariable Long bookingId) {
        log.debug("Получен PATCH-запрос к эндпоинту: /bookings на обновление статуса бронирования с id: {}", bookingId);
        return bookingService.update(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER) Long userId,
                                  @PathVariable Long bookingId) {
        log.debug("Получен GET-запрос к эндпоинту: /bookings на получение данных о бронировании с id: {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader(USER) Long userId,
                                     @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.debug("Получен GET-запрос к эндпоинту: /bookings на получение списка всех бронирований пользователя с id: {}", userId);
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(USER) Long ownerId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.debug("Получен GET-запрос к эндпоинту: /bookings на получение списка бронирований для всех вещей пользователя с id: {}", ownerId);
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
