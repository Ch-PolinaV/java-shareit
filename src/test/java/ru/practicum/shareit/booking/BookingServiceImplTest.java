package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStatusException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private final LocalDateTime created = LocalDateTime.now();
    private final LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 30);
    private final LocalDateTime end = LocalDateTime.of(2023, 10, 1, 12, 30);
    private final User owner = new User(1L, "Name", "test@test.ru");
    private final User booker = new User(2L, "Second", "test@email.ru");
    private final User user = new User(3L, "User", "user@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", booker, created);
    private final Item item = new Item(1L, "Item", "text", true, owner, itemRequest);
    private final Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
    private final InputBookingDto inputBookingDto = new InputBookingDto(start, end, item.getId());
    private final BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    @Test
    void shouldCreateBooking() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Booking expectedBooking = toBooking(inputBookingDto, booker, item);
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingService.create(booker.getId(), inputBookingDto);
        actualBooking.setId(1L);

        assertEquals(bookingDto, actualBooking);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void shouldReturnNotFoundExceptionWhenBookerIsOwner() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), inputBookingDto));
        assertEquals("Вещь не может быть забронирована ее владельцем", exception.getMessage());
    }

    @Test
    void shouldReturnValidationExceptionWhenStartIsNull() {
        inputBookingDto.setStart(null);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), inputBookingDto));
        assertEquals("Время начала и окончания должны быть указаны", exception.getMessage());
    }

    @Test
    void shouldReturnValidationExceptionWhenStartIsEqualEnd() {
        inputBookingDto.setStart(end);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), inputBookingDto));
        assertEquals("Некорректно указано время: дата окончания должна быть позже даты начала", exception.getMessage());
    }

    @Test
    void shouldReturnValidationExceptionWhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), inputBookingDto));
        assertEquals("Вещь с ID=" + item.getId() + " в данный момент не доступна для бронирования", exception.getMessage());
    }

    @Test
    public void shouldUpdateBookingStatusAPPROVED() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto updateBooking = bookingService.update(owner.getId(), true, booking.getId());

        assertEquals(BookingStatus.APPROVED, updateBooking.getStatus());
    }

    @Test
    public void shouldUpdateBookingStatusREJECTED() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto updateBooking = bookingService.update(owner.getId(), false, booking.getId());

        assertEquals(BookingStatus.REJECTED, updateBooking.getStatus());
    }

    @Test
    public void shouldReturnValidationExceptionWhenBookingStatusIsNotWAITING() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.update(owner.getId(), true, booking.getId()));
        assertEquals("Невозможно изменить статус", exception.getMessage());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenUserIsNotOwner() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.update(booker.getId(), true, booking.getId()));
        assertEquals("Подтверждение или отклонение запроса на бронирование может быть выполнено только владельцем вещи", exception.getMessage());
    }

    @Test
    void shouldReturnBookingById() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BookingDto actualBooking = bookingService.getBookingById(booker.getId(), booking.getId());

        assertEquals(bookingDto, actualBooking);
    }

    @Test
    void shouldReturnNotFoundExceptionWhenUserIdIsNotEqualsOwnerOrBooker() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(user.getId(), booking.getId()));
        assertEquals("Получение данных о бронировании может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование", exception.getMessage());
    }

    @Test
    void shouldReturnAllBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(booker.getId(), page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "ALL", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnAllCURRENTBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "CURRENT", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnAllPASTBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndBefore(
                eq(booker.getId()), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "PAST", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnAllFUTUREBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartAfter(
                eq(booker.getId()), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "FUTURE", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnAllWAITINGBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "WAITING", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnAllREJECTEDBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED, page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getAllUserBookings(booker.getId(), "REJECTED", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnUnsupportedStatusExceptionBookingsStateIsUnknown() {
        int from = 0;
        int size = 10;

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllUserBookings(booker.getId(), "UNKNOWN", from, size));
    }

    @Test
    void shouldReturnOwnerBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerId(owner.getId(), page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "ALL", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnOwnerCURRENTBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndAfter(
                eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "CURRENT", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnOwnerPASTBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBefore(
                eq(owner.getId()), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "PAST", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnOwnerFUTUREBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfter(
                eq(owner.getId()), any(LocalDateTime.class), eq(page))
        ).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "FUTURE", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnOwnerWAITINGBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING, page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "WAITING", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }

    @Test
    void shouldReturnOwnerREJECTEDBookings() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(from, size, sort);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, page, bookings.size());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED, page)).thenReturn(bookingPage);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        List<BookingDto> actualBookingsList = bookingService.getOwnerBookings(owner.getId(), "REJECTED", from, size);

        assertEquals(bookingDtos, actualBookingsList);
    }
}