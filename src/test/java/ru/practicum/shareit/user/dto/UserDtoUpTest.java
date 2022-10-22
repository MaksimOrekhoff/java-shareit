package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class UserDtoUpTest {
    @Autowired
    private JacksonTester<UserDtoUp> jacksonTester;

    @Test
    void serialization() throws IOException {
        UserDtoUp userDto = new UserDtoUp(1L);
        JsonContent<UserDtoUp> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
    }
}