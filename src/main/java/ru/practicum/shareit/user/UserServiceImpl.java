package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDB userDB;
    private final MapperUsers mapperUsers;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDB.getEmails().containsValue(userDto.getEmail())) {
            throw new UserExistException("Пользователь с таким email уже существует");
        }
        User user = userDB.createUser(userDto);
        log.debug("Добавлен пользователь: {}", user);
        return mapperUsers.toUser(user);
    }

    @Override
    public UserDto getById(long id) {
        User user = userDB.getUser(id);
        if (user == null) {
            throw new NotFoundException("Такой пользователь не сущетсвует.");
        }
        log.debug("Получен пользователь: {}", user);
        return mapperUsers.toUser(user);
    }

    @Override
    public UserDto change(Long id, UserDto userDto) {
        if (userDB.getEmails().containsKey(id)) {
            if (userDB.getEmails().containsValue(userDto.getEmail())) {
                if (userDB.getEmails().get(id).equals(userDto.getEmail())) {
                    User user = userDB.update(id, userDto);
                    return mapperUsers.toUser(user);
                }
                throw new UserExistException("Пользователь с таким email уже существует.");
            }
        }
        User newUser = userDB.update(id, userDto);
        log.debug("Данные пользователя {} обновлены ", newUser);
        return mapperUsers.toUser(newUser);
    }

    @Override
    public void remove(long id) {
        if(userDB.getEmails().containsKey(id)) {
            userDB.delete(id);
            log.debug("Удалён пользователь с id: {}", id);
        } else {
            throw new NotFoundException("Такой пользователь не сущетсвует.");
        }

    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User user : userDB.getAll()) {
            usersDto.add(mapperUsers.toUser(user));
        }
        return usersDto;
    }
}
