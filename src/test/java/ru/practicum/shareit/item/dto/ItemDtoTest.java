package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.validation.CreateValidationGroup;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testJsonUserDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "text",
                true,
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations, is(empty()));

        String jsonString = json.write(itemDto).getJson();

        assertThat(jsonString, containsString("\"id\":1"));
        assertThat(jsonString, containsString("\"name\":\"name\""));
        assertThat(jsonString, containsString("\"description\":\"text\""));
        assertThat(jsonString, containsString("\"available\":true"));
    }

    @Test
    void testJsonItemDtoWithEmptyName() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "",
                "text",
                true,
                null,
                null,
                null,
                null
        );
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, CreateValidationGroup.class);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(itemDto).getJson();
        System.out.println(jsonString);
    }

    @Test
    void testJsonItemDtoWithAvailableIsNull() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "text",
                null,
                null,
                null,
                null,
                null
        );
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, CreateValidationGroup.class);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(itemDto).getJson();
        System.out.println(jsonString);
    }
}
