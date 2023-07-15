package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.AlreadyExistsException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1L;

    @Override
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (users.get(id) == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id: {} уже существует", user.getId());
            throw new AlreadyExistsException("Пользователь с указанным id уже был добавлен ранее");
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.info("email: {} уже существует", user.getEmail());
            throw new AlreadyExistsException("Email: " + user.getEmail() + " уже используется другим пользователем");
        }
        log.info("Добавлен новый пользователь: {}", user);
        user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        getUserById(user.getId());

        if (!users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            log.info("email: {} уже существует", user.getEmail());
            throw new AlreadyExistsException("Email: " + user.getEmail() + " уже используется другим пользователем");
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        log.info("Пользователь с id: {} обновлен", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long id) {
        getUserById(id);
        return users.remove(id);
    }

    private long createId() {
        return id++;
    }
}
