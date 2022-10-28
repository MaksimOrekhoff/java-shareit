package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.unsorted());
        log.info("Получен Get-запрос на получение всех пользователей.");
        return userService.getAllUsers(pageRequest);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("Получен Get-запрос на получение пользователя c id: {}", id);
        return userService.getById(id);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.debug("Получен Post-запрос на добавление пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Получен Patch-запрос на обновление данных пользователя {}", userDto);
        return userService.change(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с id: {}.", userId);
        userService.remove(userId);
    }
}
