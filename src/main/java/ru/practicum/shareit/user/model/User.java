package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    @Email(message = "Электронная почта должна соответствовать формату электронного адреса")
    private String email;
}
