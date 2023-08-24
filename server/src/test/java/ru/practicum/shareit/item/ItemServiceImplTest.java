package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private final LocalDateTime created = LocalDateTime.now();
    private final LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 30);
    private final LocalDateTime end = LocalDateTime.of(2023, 10, 1, 12, 30);

    private final User owner = new User(1L, "Name", "test@test.ru");
    private final User user = new User(2L, "Second", "test@email.ru");

    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, created);

    private final Item item = new Item(1L, "Item", "text", true, owner, itemRequest);

    private final Comment comment = new Comment(1L, "text", item, user, created);
    private final CommentDto commentDto = ItemMapper.toCommentDto(comment);
    private final List<Comment> comments = List.of(comment);
    private final List<CommentDto> commentDtoList = List.of(commentDto);

    private final ItemDto itemDto = ItemMapper.toItemDto(item, comments);

    private final Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
    private final Booking bookingNext = new Booking(2L, start.plusHours(2), end.plusHours(2), item, user, BookingStatus.APPROVED);
    private final BookingForItemDto lastBookingDto = BookingMapper.toBookingForItemDto(booking);
    private final BookingForItemDto nextBookingDto = BookingMapper.toBookingForItemDto(bookingNext);

    private final ItemDto itemDtoWithBookings = new ItemDto(1L, "Item", "text", true, lastBookingDto, nextBookingDto, commentDtoList, itemRequest.getId());

    @Test
    void shouldCreateItemWithItemRequest() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItem = itemService.create(itemDto, owner.getId());
        assertEquals(itemDto, actualItem);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void shouldCreateItemWithoutItemRequest() {
        Item itemWithoutItemRequest = new Item(2L, "Item", "text", true, owner, null);
        Comment comment1 = new Comment(2L, "text", itemWithoutItemRequest, user, created);

        ItemDto newItemDto = ItemMapper.toItemDto(itemWithoutItemRequest, List.of(comment1));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(commentRepository.findByItemId(itemWithoutItemRequest.getId())).thenReturn(List.of(comment1));
        when(itemRepository.save(itemWithoutItemRequest)).thenReturn(itemWithoutItemRequest);

        ItemDto actualItem = itemService.create(newItemDto, owner.getId());
        assertEquals(newItemDto, actualItem);
        verify(itemRepository, times(1)).save(itemWithoutItemRequest);
    }

    @Test
    public void shouldUpdateItem() {
        Item newItem = new Item();
        newItem.setName("New Name");
        newItem.setDescription("newText");
        newItem.setAvailable(false);
        ItemDto newItemDto = ItemMapper.toItemDto(newItem, comments);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto updateItem = itemService.update(owner.getId(), newItemDto, item.getId());

        assertEquals("New Name", updateItem.getName());
        assertEquals("newText", updateItem.getDescription());
        assertEquals(false, updateItem.getAvailable());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenUserIsNotItemOwner() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), itemDto, item.getId()));
    }

    @Test
    public void shouldReturnItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        when(bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class))).thenReturn(booking);
        when(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(bookingNext);

        ItemDto actualItem = itemService.getItemById(owner.getId(), item.getId());

        assertEquals(itemDtoWithBookings, actualItem);
    }

    @Test
    public void shouldReturnItemByOwner() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        List<Item> items = List.of(item);
        Page<Item> itemPage = new PageImpl<>(items, page, items.size());
        when(itemRepository.findByOwner_Id(owner.getId(), page)).thenReturn(itemPage);
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        when(bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class))).thenReturn(booking);
        when(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(bookingNext);

        List<ItemDto> actualItems = itemService.getItemsByOwner(owner.getId(), from, size);

        assertEquals(List.of(itemDtoWithBookings), actualItems);
    }

    @Test
    public void shouldReturnItemBySearchQuery() {
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from, size);
        List<Item> items = List.of(item);
        Page<Item> itemPage = new PageImpl<>(items, page, items.size());
        when(itemRepository.findBySearchQuery("text", page)).thenReturn(itemPage);
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);

        List<ItemDto> actualItems = itemService.getItemsBySearchQuery("text", from, size);

        assertEquals(List.of(itemDto), actualItems);
    }

    @Test
    public void shouldReturnEmptyListOfItemBySearchQueryWhenTextIsBlank() {
        int from = 0;
        int size = 10;

        List<ItemDto> actualItems = itemService.getItemsBySearchQuery("", from, size);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void shouldCreateComment() {
        LocalDateTime currentTime = LocalDateTime.now();

        CommentDto commentDto = new CommentDto(1L, "text", currentTime, user.getName());
        Comment comment = new Comment(1L, commentDto.getText(), item, user, commentDto.getCreated());
        Booking oldBooking = new Booking(1L, currentTime.minusHours(2), currentTime.minusHours(1), item, user, BookingStatus.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(eq(item.getId()), eq(user.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(oldBooking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualComment = itemService.createComment(user.getId(), commentDto, item.getId());

        assertEquals(commentDto, actualComment);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void shouldReturnValidationExceptionCreateCommentIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(eq(item.getId()), eq(user.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(any(Booking.class));

        ValidationException exception = assertThrows(ValidationException.class, () -> itemService.createComment(user.getId(), commentDto, item.getId()));
        assertEquals("Отзывы могут оставлять только пользователи, которые брали вещь в аренду", exception.getMessage());
    }
}