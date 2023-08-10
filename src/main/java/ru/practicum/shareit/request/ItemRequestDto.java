package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto implements Serializable {
    private Long id;
    @NotEmpty
    private String description;
    private Long requestor;
    private LocalDateTime created;
    //private List<ItemDto> items;
}
