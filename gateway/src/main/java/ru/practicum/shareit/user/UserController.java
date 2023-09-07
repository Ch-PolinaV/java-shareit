package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.CreateValidationGroup;
import ru.practicum.shareit.validation.UpdateValidationGroup;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /users на получение всех пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.debug("Получен GET-запрос к эндпоинту: /users/{} на получение пользователя с id={}", id, id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(CreateValidationGroup.class) @RequestBody UserDto userDto) {
        log.debug("Получен POST-запрос к эндпоинту: /users на создание нового пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated(UpdateValidationGroup.class) @RequestBody UserDto userDto,
                          @PathVariable Long id) {
        log.debug("Получен Patch-запрос к эндпоинту: /users на обновление или создание пользователя");
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.debug("Получен DELETE-запрос к эндпоинту: /users/{} на удаление пользователя с id={}", id, id);
        return userClient.delete(id);
    }
}
