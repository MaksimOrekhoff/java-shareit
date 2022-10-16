package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.StatusItem;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        BookingDto bookingDto = new BookingDto(1L, 1L, LocalDateTime.now().plusHours(1).withNano(0),
                LocalDateTime.now().plusHours(2).withNano(0), StatusItem.WAITING);
        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(bookingDto.getStatus().toString());

    }
}