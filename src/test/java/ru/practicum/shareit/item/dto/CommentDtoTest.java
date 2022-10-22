package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        CommentDto c = new CommentDto(1L, "comment", "John", LocalDateTime.now().plusMonths(1).withNano(0));
        JsonContent<CommentDto> result = jacksonTester.write(c);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(c.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.text").isEqualTo(c.getText());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(c.getCreated().toString());
        assertThat(result).extractingJsonPathValue("$.authorName").isEqualTo(c.getAuthorName());
    }
}