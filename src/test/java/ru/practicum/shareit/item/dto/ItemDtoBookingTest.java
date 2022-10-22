package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoBookingTest {
    @Autowired
    private JacksonTester<ItemDtoBooking> jacksonTester;

    @Test
    void serialization() throws IOException {
        BookingDtoItem last = new BookingDtoItem(1L, 1L);
        BookingDtoItem next = new BookingDtoItem(2L, 2L);
        ItemDtoBooking itemDto = new ItemDtoBooking(1L, "comment",
                "John", true, last, next, new ArrayList<>());
        JsonContent<ItemDtoBooking> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(itemDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(itemDto.getNextBooking().getId().intValue());

    }
}

