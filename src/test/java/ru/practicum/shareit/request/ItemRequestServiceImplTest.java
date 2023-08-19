package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    ItemService itemService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private final LocalDateTime created = LocalDateTime.now();
    private final User user = new User(1L, "Name", "test@test.ru");
    private final User secondUser = new User(2L, "Second", "test@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, created);
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);

    @Test
    void shouldCreateItemRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto actualRequest = itemRequestService.create(itemRequestDto, user.getId(), created);

        assertEquals(itemRequestDto, actualRequest);
        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    public void shouldNotCreateItemRequestAndReturnNotFoundException() {
        when(userRepository.findById(user.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDto, user.getId(), created));
    }

    @Test
    void shouldReturnAllOwnItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(requests);
        when(itemService.getItemsByRequest(itemRequest.getId())).thenReturn(null);

        List<ItemRequestDto> requestDtos = requests.stream()
                .map(ItemRequestMapper -> toItemRequestDto(ItemRequestMapper, itemService.getItemsByRequest(ItemRequestMapper.getId())))
                .collect(Collectors.toList());
        List<ItemRequestDto> actualRequestsList = itemRequestService.getOwnItemRequests(user.getId());

        assertEquals(requestDtos, actualRequestsList);
    }

    @Test
    void shouldReturnAllItemRequests() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        List<ItemRequest> requests = List.of(itemRequest);
        Page<ItemRequest> requestsPage = new PageImpl<>(requests, page, requests.size());

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(secondUser.getId(), page)).thenReturn(requestsPage);
        when(itemService.getItemsByRequest(itemRequest.getId())).thenReturn(null);

        List<ItemRequestDto> requestDtos = requests.stream()
                .map(ItemRequestMapper -> toItemRequestDto(ItemRequestMapper, itemService.getItemsByRequest(ItemRequestMapper.getId())))
                .collect(Collectors.toList());
        List<ItemRequestDto> actualRequestsList = itemRequestService.getAllItemRequests(secondUser.getId(),from, size);

        assertEquals(requestDtos, actualRequestsList);
    }

    @Test
    void shouldReturnItemRequestById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        itemRequestDto.setItems(new ArrayList<>());
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto actualRequest = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDto, actualRequest);
    }
}