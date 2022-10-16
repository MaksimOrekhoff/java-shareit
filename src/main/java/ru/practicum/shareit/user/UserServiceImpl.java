package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userDB;
    private final MapperUsers mapperUsers;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userDB.save(new User(userDto.getId(), userDto.getName(), userDto.getEmail()));
        log.debug("Добавлен пользователь: {}", user);
        return mapperUsers.toUserDto(user);
    }

    @Override
    public UserDto getById(long id) {
        Optional<User> user = userDB.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Такой пользователь не сущетсвует.");
        }
        log.debug("Получен пользователь: {}", user);
        return mapperUsers.toUserDto(user.get());
    }

    @Override
    public UserDto change(Long id, UserDto userDto) {
        Optional<User> user = userDB.findById(id);
        if (user.isPresent()) {
            User newUser = new User(id,
                    userDto.getName() == null ? user.get().getName() : userDto.getName(),
                    userDto.getEmail() == null ? user.get().getEmail() : userDto.getEmail());
            newUser = userDB.save(newUser);
            log.debug("Данные пользователя {} обновлены ", newUser);
            return mapperUsers.toUserDto(newUser);
        }
        throw new NotFoundException("Not found");
    }

    @Override
    public void remove(long id) {
        userDB.deleteById(id);
        log.debug("Удалён пользователь с id: {}", id);
    }

    @Override
    public Collection<UserDto> getAllUsers(PageRequest pageRequest) {
        return userDB.findAll(pageRequest).stream()
                .map(mapperUsers::toUserDto)
                .collect(Collectors.toList());
    }
}
