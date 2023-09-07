package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exeption.AlreadyExistsException;
import ru.practicum.shareit.exeption.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final User user = new User(1L, "Name", "test@test.ru");
    private final UserDto userDto = UserMapper.toUserDto(user);

    @Test
    public void shouldReturnAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        List<UserDto> actualUsersList = userService.findAll();

        assertEquals(userDtos, actualUsersList);
    }

    @Test
    public void shouldReturnUserById() {
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(id);

        assertEquals(userDto, actualUser);
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenUserIsNotExist() {
        long id = 0L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    public void shouldCreateUser() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.create(userDto);

        assertEquals(userDto, actualUserDto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldNotCreateUserAndReturnAlreadyExistsException() {
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlreadyExistsException.class, () -> userService.create(userDto));
    }

    @Test
    public void shouldUpdateUser() {
        long id = 1L;
        User newUser = new User();
        newUser.setName("New Name");
        newUser.setEmail("newEmail@test.ru");
        UserDto newUserDto = UserMapper.toUserDto(newUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto savedUser = userService.update(newUserDto, id);

        assertEquals("New Name", savedUser.getName());
        assertEquals("newEmail@test.ru", savedUser.getEmail());
    }

    @Test
    public void shouldReturnAlreadyExistsExceptionWhenUpdateUserWithExistEmail() {
        long id = 2L;
        String existEmail = "newEmail@test.ru";
        User newUser = new User(2L, "Second User", "new@email.ru");
        User updateUser = new User();
        updateUser.setEmail(existEmail);
        UserDto newUserDto = UserMapper.toUserDto(updateUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(newUser));
        when(userRepository.existsByEmail(existEmail)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> userService.update(newUserDto, id));
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
    }

    @Test
    public void shouldDeleteUserById() {
        long id = 1L;

        userService.delete(id);
        when(userRepository.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository, times(1)).deleteById(id);
    }
}
