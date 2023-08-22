package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testJsonItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,
                "text",
                1L,
                LocalDateTime.parse("2023-08-11T12:00:00"),
                null
        );

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);
        assertThat(violations, is(empty()));

        String jsonString = json.write(itemRequestDto).getJson();

        assertThat(jsonString, containsString("\"id\":1"));
        assertThat(jsonString, containsString("\"description\":\"text\""));
        assertThat(jsonString, containsString("\"requestor\":1"));
        assertThat(jsonString, containsString("\"created\":\"2023-08-11T12:00:00\""));
    }

    @Test
    void testJsonItemRequestDtoWithEmptyDescription() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,
                "",
                1L,
                LocalDateTime.parse("2023-08-11T12:00:00"),
                null
        );

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertFalse(violations.isEmpty());
    }
}