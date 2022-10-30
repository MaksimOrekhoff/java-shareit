package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class BookingDtoItemTest {
    @Autowired
    private JacksonTester<BookingDtoItem> jacksonTester;

    @Test
    void serialization() throws IOException {
        BookingDtoItem bookingDto = new BookingDtoItem(1L, 2L);
        JsonContent<BookingDtoItem> result = jacksonTester.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookingDto.getBookerId().intValue());

    }
}