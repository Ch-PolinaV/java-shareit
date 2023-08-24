package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String name;
    private String email;
}
