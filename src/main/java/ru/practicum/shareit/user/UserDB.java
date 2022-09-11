package ru.practicum.shareit.user;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Getter
public class UserDB {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private long id = 0;

    public User createUser(UserDto userDto) {
        id++;
        User user = toUserDto(userDto, id);
        users.put(id, user);
        emails.put(id, user.getEmail());
        return user;
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public User update(Long id, UserDto userDto) {
        User user = users.get(id);
        User newUser = new User(user.getId(),
                userDto.getName() == null ? user.getName() : userDto.getName(),
                userDto.getEmail() == null ? user.getEmail() : userDto.getEmail());
        users.put(id, newUser);
        emails.put(id, newUser.getEmail());
        return newUser;
    }

    public void delete(long id) {
        users.remove(id);
        emails.remove(id);
    }

    public Collection<User> getAll() {
        return users.values();
    }

    private User toUserDto(UserDto userDto, long id) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }


}
