package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto implements Serializable {
    private Long id;
    @NotEmpty
    private String text;
    private LocalDateTime created;
    private String authorName;
}
