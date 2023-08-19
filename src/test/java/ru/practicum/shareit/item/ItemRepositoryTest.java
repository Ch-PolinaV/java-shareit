package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void addItems() {
        User owner = userRepository.save(new User(1L, "Name", "test@test.ru"));
        itemRepository.save(new Item(1L, "Item", "text", true, owner, null));
        itemRepository.save(new Item(2L, "Name", "description", true, owner, null));
    }

    @AfterEach
    void deleteItems() {
        itemRepository.deleteAll();
    }

    @Test
    void findBySearchQuery() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        Page<Item> items = itemRepository.findBySearchQuery("text", page);
        List<Item> itemList = items.toList();

        assertEquals(1, itemList.size());
        assertEquals("Item", itemList.get(0).getName());
    }
}