package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен Get-запрос на получение всех пользователей.");
        return userClient.getAllUsers(from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Получен Get-запрос на получение пользователя c id: {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен Post-запрос на добавление пользователя");
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable @Valid long id, @RequestBody UserDto userDto) {
        log.info("Получен Patch-запрос на обновление данных пользователя {}", userDto);
        return userClient.patchUser(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с id: {}.", userId);
        userClient.deleteUser(userId);
    }
}
