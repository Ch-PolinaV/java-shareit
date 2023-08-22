package ru.practicum.shareit.item.dto;

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
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testJsonCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(1L,
                "text",
                LocalDateTime.parse("2023-08-11T12:00:00"),
                "name"
        );

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertThat(violations, is(empty()));

        String jsonString = json.write(commentDto).getJson();

        assertThat(jsonString, containsString("\"id\":1"));
        assertThat(jsonString, containsString("\"text\":\"text\""));
        assertThat(jsonString, containsString("\"created\":\"2023-08-11T12:00:00\""));
        assertThat(jsonString, containsString("\"authorName\":\"name\""));
    }

    @Test
    void testJsonCommentDtoWithEmptyText() throws Exception {
        CommentDto commentDto = new CommentDto(1L,
                "",
                LocalDateTime.parse("2023-08-11T12:00:00"),
                "name"
        );

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(commentDto).getJson();
        System.out.println(jsonString);
    }
}