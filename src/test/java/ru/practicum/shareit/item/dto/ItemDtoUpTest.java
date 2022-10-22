package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class ItemDtoUpTest {
    @Autowired
    private JacksonTester<ItemDtoUp> jacksonTester;

    @Test
    void serialization() throws IOException {
        ItemDtoUp itemDto = new ItemDtoUp(1L, "John");
        JsonContent<ItemDtoUp> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)itemDto.getId());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDto.getName());
    }
}