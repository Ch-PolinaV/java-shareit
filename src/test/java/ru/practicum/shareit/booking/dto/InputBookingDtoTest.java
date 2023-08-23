package ru.practicum.shareit.booking.dto;

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
class InputBookingDtoTest {

    @Autowired
    private JacksonTester<InputBookingDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testJsonBookingDto() throws Exception {
        InputBookingDto inputBookingDto = new InputBookingDto(
                LocalDateTime.parse("2023-12-25T12:00:00"),
                LocalDateTime.parse("2023-12-26T12:00:00"),
                1L
        );

        Set<ConstraintViolation<InputBookingDto>> violations = validator.validate(inputBookingDto);
        assertThat(violations, is(empty()));

        String jsonString = json.write(inputBookingDto).getJson();

        assertThat(jsonString, containsString("\"start\":\"2023-12-25T12:00:00\""));
        assertThat(jsonString, containsString("\"end\":\"2023-12-26T12:00:00\""));
        assertThat(jsonString, containsString("\"itemId\":1"));
    }

    @Test
    void testJsonBookingDtoWithInvalidData() throws Exception {
        InputBookingDto inputBookingDto = new InputBookingDto(
                LocalDateTime.now().minusMinutes(30),
                LocalDateTime.parse("2023-12-26T12:00:00"),
                1L
        );

        Set<ConstraintViolation<InputBookingDto>> violations = validator.validate(inputBookingDto);

        assertFalse(violations.isEmpty());

        String jsonString = json.write(inputBookingDto).getJson();
        System.out.println(jsonString);
    }
}
