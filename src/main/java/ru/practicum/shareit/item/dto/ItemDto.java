package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.CreateValidationGroup;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty(groups = CreateValidationGroup.class)
    private String name;
    @NotEmpty(groups = CreateValidationGroup.class)
    private String description;
    @NotNull(groups = CreateValidationGroup.class)
    private Boolean available;
    private ItemRequest request;
}
