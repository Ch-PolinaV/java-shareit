package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwner_Id(Long id, Pageable pageable);

    List<Item> findByRequest_IdOrderById(Long id);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND (LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :text, '%')))")
    Page<Item> findBySearchQuery(@Param("text") String text, Pageable pageable);
}
