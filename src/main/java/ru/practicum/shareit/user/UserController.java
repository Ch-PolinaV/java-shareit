package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.debug("Получен GET-запрос к эндпоинту: /users на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.debug("Получен GET-запрос к эндпоинту: /users/{} на получение пользователя с id={}", id, id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен POST-запрос к эндпоинту: /users на создание нового пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        log.debug("Получен Patch-запрос к эндпоинту: /users на обновление или создание пользователя");
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable long id) {
        log.debug("Получен DELETE-запрос к эндпоинту: /users/{} на удаление пользователя с id={}", id, id);
        return userService.delete(id);
    }
}
