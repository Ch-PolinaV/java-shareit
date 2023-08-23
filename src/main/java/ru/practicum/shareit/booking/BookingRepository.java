package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerId(Long userId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    Booking findFirstByItem_IdAndStartBeforeOrderByEndDesc(Long itemId, LocalDateTime startTime);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime startTime, BookingStatus status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId, LocalDateTime end, BookingStatus status);
}
