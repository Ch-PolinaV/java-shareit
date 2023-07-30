package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@AllArgsConstructor
public class CommentDto implements Serializable {
    private Long id;
    @NotEmpty
    private String text;
    @JsonIgnore
    private Item item;
    private LocalDateTime created;
    private String authorName;
}
