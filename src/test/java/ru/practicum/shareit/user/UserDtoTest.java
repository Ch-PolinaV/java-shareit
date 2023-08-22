package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.validation.CreateValidationGroup;
import ru.practicum.shareit.validation.UpdateValidationGroup;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testJsonUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "test@test.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations, is(empty()));

        String jsonString = json.write(userDto).getJson();

        assertThat(jsonString, containsString("\"id\":1"));
        assertThat(jsonString, containsString("\"name\":\"name\""));
        assertThat(jsonString, containsString("\"email\":\"test@test.ru\""));
    }

    @Test
    void testJsonUserDtoWithInvalidEmail() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "test");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, UpdateValidationGroup.class);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(userDto).getJson();
        System.out.println(jsonString);
    }

    @Test
    void testJsonUserDtoWithEmptyName() throws Exception {
        UserDto userDto = new UserDto(1L, "", "test@test");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, CreateValidationGroup.class);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(userDto).getJson();
        System.out.println(jsonString);
    }
}