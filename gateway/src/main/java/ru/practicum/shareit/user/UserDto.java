package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreateValidationGroup;
import ru.practicum.shareit.validation.UpdateValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(groups = CreateValidationGroup.class)
    private String name;
    @Email(message = "Электронная почта должна соответствовать формату электронного адреса",
            groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    @NotEmpty(groups = CreateValidationGroup.class)
    private String email;
}
