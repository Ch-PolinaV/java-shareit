package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User requestor;
    private User user;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(new User(1L, "Requestor", "test@test.ru"));
        user = userRepository.save(new User(2L, "user", "mail@test.ru"));
        ItemRequest itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "text", requestor, LocalDateTime.now()));
    }

    @AfterEach
    void tearDown() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertEquals(1, itemRequests.size());
        assertEquals("Requestor", itemRequests.get(0).getRequestor().getName());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId(), page);
        List<ItemRequest> itemRequestList = itemRequests.toList();

        assertEquals(1, itemRequestList.size());
        assertEquals("Requestor", itemRequestList.get(0).getRequestor().getName());
    }
}