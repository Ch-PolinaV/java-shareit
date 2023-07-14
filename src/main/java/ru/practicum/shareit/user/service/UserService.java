package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    public UserDto create(User user) {
        return userMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(User user, Long id) {
        if (user.getId() == null) {
            user.setId(id);
        }
        return userMapper.toUserDto(userStorage.update(user));
    }

    public UserDto delete(Long id) {
        return userMapper.toUserDto(userStorage.delete(id));
    }
}
