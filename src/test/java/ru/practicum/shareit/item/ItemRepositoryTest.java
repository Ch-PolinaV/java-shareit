package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;

    @BeforeEach
    void addItems() {
        owner = userRepository.save(new User(1L, "Name", "test@test.ru"));
        User requestor = userRepository.save(new User(2L, "Requestor", "mail@test.ru"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "text", requestor, LocalDateTime.now()));
        item1 = itemRepository.save(new Item(1L, "Item", "text", true, owner, null));
        item2 = itemRepository.save(new Item(2L, "Name", "description", true, owner, null));
    }

    @AfterEach
    void deleteItems() {
        itemRepository.deleteAll();
    }

    @Test
    void findByOwner_Id() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        Page<Item> items = itemRepository.findByOwner_Id(owner.getId(), page);
        List<Item> itemList = items.toList();

        assertEquals(2, itemList.size());
        assertEquals("Item", itemList.get(0).getName());
        assertEquals("Name", itemList.get(1).getName());
    }

    @Test
    void findByRequest_IdOrderById() {
        item1.setRequest(itemRequest);
        item2.setRequest(itemRequest);
        List<Item> itemList = itemRepository.findByRequest_IdOrderById(itemRequest.getId());

        assertEquals(2, itemList.size());
        assertEquals("Item", itemList.get(0).getName());
        assertEquals("Name", itemList.get(1).getName());
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