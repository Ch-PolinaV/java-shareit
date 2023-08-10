package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(Long requestorId);

    Page<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(Long requestorId, Pageable pageable);
}
