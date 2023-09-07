package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long id);
}
