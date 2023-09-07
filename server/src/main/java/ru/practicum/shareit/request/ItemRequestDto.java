package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto implements Serializable {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemForItemRequestDto> items;
}
