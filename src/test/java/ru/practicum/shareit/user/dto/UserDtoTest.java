package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        UserDto userDto = new UserDto(1L, "user", "User@mail.com");
        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int)userDto.getId());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathValue("$.email").isEqualTo(userDto.getEmail());
    }
}