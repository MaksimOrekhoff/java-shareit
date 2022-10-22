package ru.practicum.shareit.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto getById(long id);

    UserDto change(Long id, UserDto userDto);

    void remove(long id);

    Collection<UserDto> getAllUsers(PageRequest pageRequest);

}
