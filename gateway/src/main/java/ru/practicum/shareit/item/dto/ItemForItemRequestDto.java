package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ItemForItemRequestDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}
