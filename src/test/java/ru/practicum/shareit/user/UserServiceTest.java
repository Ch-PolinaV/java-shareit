package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void createUser() {
        userDto = userService.create(new UserDto(1L, "name", "test@test"));
    }

    @Test
    void getUserById() {
        UserDto actualUser = userService.getUserById(userDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo(user.getName()));
        assertThat(actualUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void create() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        userDto.setName("NewName");
        UserDto newUserDto = userService.update(userDto, userDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto.getEmail()));
    }
}