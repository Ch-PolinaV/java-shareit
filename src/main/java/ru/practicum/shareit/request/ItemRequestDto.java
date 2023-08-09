package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto implements Serializable {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
