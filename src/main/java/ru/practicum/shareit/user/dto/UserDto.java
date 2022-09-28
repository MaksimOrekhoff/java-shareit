package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private final long id;
    private final String name;
    @NotEmpty
    @Email(message = "Некорректный формат email")
    private final String email;
}
