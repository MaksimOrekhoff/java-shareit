package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceImplTest {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    UserDto userDto;

    @Test
    void create() {
        UserDto newUser = userService.create(userDto);

        assertEquals(newUser.getName(), userDto.getName());
        assertEquals(newUser.getEmail(), userDto.getEmail());
    }

    @Test
    void findByIdExceptionUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> userService.getById(100L));

        Assertions.assertEquals("Такой пользователь не сущетсвует.", thrown.getMessage());
    }


    @Test
    void findByIdUser() {
        UserDto newUser = userService.create(userDto);

        UserDto result = userService.getById(newUser.getId());

        assertEquals(newUser.getId(), result.getId());
        assertEquals(newUser.getName(), result.getName());
        assertEquals(newUser.getEmail(), result.getEmail());
    }

    @Test
    void change() {
        UserDto newUser = userService.create(userDto);
        newUser.setEmail("newEmail");
        UserDto result = userService.change(newUser.getId(), newUser);

        assertEquals(newUser.getId(), result.getId());
        assertEquals(newUser.getName(), result.getName());
        assertEquals(newUser.getEmail(), result.getEmail());

        newUser.setName("newName");

        result = userService.change(newUser.getId(), newUser);

        assertEquals(newUser.getId(), result.getId());
        assertEquals(newUser.getName(), result.getName());
        assertEquals(newUser.getEmail(), result.getEmail());
    }

    @Test
    void changeByIdExcep() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> userService.change(100L, userDto));

        Assertions.assertEquals("Not found", thrown.getMessage());
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @BeforeEach
    void start() {
        userDto = new UserDto(1L, "user1", "user1@email.com");
    }

    @Test
    void remove_fail() {
        //Assign
        UserDto newUser = userService.create(userDto);
        UserDto result = userService.getById(newUser.getId());

        assertNotNull(result);
        assertEquals(newUser.getId(), result.getId());
        assertEquals(newUser.getName(), result.getName());
        assertEquals(newUser.getEmail(), result.getEmail());
        //Act
        userService.remove(result.getId());
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> userService.getById(result.getId()));
        //Assert
        Assertions.assertEquals("Такой пользователь не сущетсвует.", thrown.getMessage());
    }

    @Test
    void allUsers() {
        //Assign
        UserDto newUser = userService.create(userDto);
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());
        //Act
        List<UserDto> userDtos = (List<UserDto>) userService.getAllUsers(myPageRequest);

        //Assert
        assertFalse(userDtos.isEmpty());
        assertEquals(newUser.getName(), userDtos.get(0).getName());
        assertEquals(newUser.getEmail(), userDtos.get(0).getEmail());
    }


}