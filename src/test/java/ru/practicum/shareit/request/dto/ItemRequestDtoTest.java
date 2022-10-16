package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        ItemRequestDto itemDto = new ItemRequestDto(1L, "John", LocalDateTime.now().plusMonths(1).withNano(0));
        JsonContent<ItemRequestDto> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)itemDto.getId());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(itemDto.getCreated().toString());
    }
}