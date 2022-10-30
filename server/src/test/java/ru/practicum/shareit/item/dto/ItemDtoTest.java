package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        ItemDto itemDto = new ItemDto(1L, "comment", "John", true, 1L);
        JsonContent<ItemDto> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)itemDto.getId());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
    }
}