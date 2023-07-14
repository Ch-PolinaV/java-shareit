package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User getUserById(Long id);

    User create(User user);

    User update(User user);

    User delete(Long id);
}
