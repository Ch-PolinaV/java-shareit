package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByBookerId(Long userId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByItemOwnerId(Long ownerId);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime endTime);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime startTime, BookingStatus status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId, LocalDateTime end, BookingStatus status);
}
