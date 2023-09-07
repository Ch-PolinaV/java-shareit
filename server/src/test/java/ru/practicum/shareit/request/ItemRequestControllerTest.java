package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private static final String USER = "X-Sharer-User-Id";
    private final User user = new User(1L, "User", "user@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
    private final ItemForItemRequestDto item = new ItemForItemRequestDto(1L, "Item", "text", itemRequest.getId(), true);
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, List.of(item));

    @SneakyThrows
    @Test
    void create() {
        when(itemRequestService.create(any(), any(Long.class), any(LocalDateTime.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void createInvalidItemRequest() {
        when(itemRequestService.create(any(), any(Long.class), any(LocalDateTime.class))).thenThrow(new ValidationException("Недопустимое значение"));

        ItemRequestDto invalidItemRequestDto = new ItemRequestDto(
                1L,
                "",
                1L,
                LocalDateTime.now(),
                Collections.emptyList()
        );

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER, 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Недопустимое значение")));
    }

    @SneakyThrows
    @Test
    void getOwnItemRequests() {
        when(itemRequestService.getOwnItemRequests(any(Long.class))).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getAllItemRequests() {
        when(itemRequestService.getAllItemRequests(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER, 1)
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(itemRequestService.getItemRequestById(any(Long.class), any(Long.class))).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}