package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User owner;
    private User booker;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private LocalDateTime currentTime;
    private PageRequest page;

    @BeforeEach
    void setUp() {
        currentTime = LocalDateTime.now();
        page = PageRequest.of(0, 10);
        owner = userRepository.save(new User(1L, "Name", "test@test.ru"));
        booker = userRepository.save(new User(2L, "Requestor", "mail@test.ru"));
        item1 = itemRepository.save(new Item(1L, "Item", "text", true, owner, null));
        item2 = itemRepository.save(new Item(2L, "Name", "description", true, owner, null));
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(), currentTime, currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndEndBefore() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(4), currentTime.minusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByBookerIdAndEndBefore(booker.getId(), currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndStartAfter() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByBookerIdAndStartAfter(booker.getId(), currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndStatus() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.REJECTED));

        Page<Booking> waitigPage = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, page);
        Page<Booking> rejectedPage = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED, page);
        List<Booking> waitingBookings = waitigPage.toList();
        List<Booking> rejectedBookings = rejectedPage.toList();

        assertEquals(1, waitingBookings.size());
        assertEquals(1, rejectedBookings.size());
        assertEquals(booking1.getId(), waitingBookings.get(0).getId());
        assertEquals(booking2.getId(), rejectedBookings.get(0).getId());
    }

    @Test
    void findByBookerId() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByBookerId(booker.getId(), page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(2, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndAfter() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndAfter(owner.getId(), currentTime, currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndEndBefore() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(2), currentTime.minusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndEndBefore(owner.getId(), currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndStartAfter() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByItemOwnerIdAndStartAfter(owner.getId(), currentTime, page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    void findByItemOwnerIdAndStatus() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.REJECTED));

        Page<Booking> waitigPage = bookingRepository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING, page);
        Page<Booking> rejectedPage = bookingRepository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED, page);
        List<Booking> waitingBookings = waitigPage.toList();
        List<Booking> rejectedBookings = rejectedPage.toList();

        assertEquals(1, waitingBookings.size());
        assertEquals(1, rejectedBookings.size());
        assertEquals(booking1.getId(), waitingBookings.get(0).getId());
        assertEquals(booking2.getId(), rejectedBookings.get(0).getId());
    }

    @Test
    void findByItemOwnerId() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item2, booker, BookingStatus.APPROVED));

        Page<Booking> bookingPage = bookingRepository.findByItemOwnerId(owner.getId(), page);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(2, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    void findFirstByItem_IdAndStartBeforeOrderByEndDesc() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item1, booker, BookingStatus.APPROVED));

        Booking booking = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(item1.getId(), currentTime);

        assertEquals(booking1, booking);
        assertEquals(booking1.getId(), booking.getId());
    }

    @Test
    void findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(1), currentTime.plusHours(1), item1, booker, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item1, booker, BookingStatus.APPROVED));

        Booking booking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(item1.getId(), currentTime, BookingStatus.APPROVED);

        assertEquals(booking2, booking);
        assertEquals(booking2.getId(), booking.getId());
    }

    @Test
    void findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus() {
        booking1 = bookingRepository.save(new Booking(1L, currentTime.minusHours(3), currentTime.minusHours(1), item1, booker, BookingStatus.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, currentTime.plusHours(3), currentTime.plusHours(4), item1, booker, BookingStatus.APPROVED));
        Booking booking3 = bookingRepository.save(new Booking(3L, currentTime.minusHours(6), currentTime.minusHours(4), item1, booker, BookingStatus.APPROVED));

        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(item1.getId(), booker.getId(), currentTime, BookingStatus.APPROVED);

        assertEquals(booking3, booking);
        assertEquals(booking3.getId(), booking.getId());
    }
}