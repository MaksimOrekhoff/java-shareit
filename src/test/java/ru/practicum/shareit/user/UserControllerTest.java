package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    UserDto userDto;
    private final ObjectMapper mapper = new ObjectMapper();
    String path = "/users";
    String pathId = "/users/1";

    @BeforeEach
    void startParam() {
        userDto = new UserDto(
                1L,
                "John",
                "johndoe@email.com");
    }

    @Test
    void getAllUsersEmpty() throws Exception {
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());
        when(userService.getAllUsers(myPageRequest))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1))
                .getAllUsers(myPageRequest);
    }

    @Test
    void getAllUsers() throws Exception {
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());
        when(userService.getAllUsers(myPageRequest))
                .thenReturn(Collections.singletonList(userDto));

        mockMvc.perform(get(path)
                        .content(mapper.writeValueAsString(userDto))
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

        verify(userService, times(1))
                .getAllUsers(myPageRequest);
    }

    @Test
    void getById() throws Exception {
        when(userService.getById(1L))
                .thenReturn(userDto);

        mockMvc.perform(get(pathId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .getById(1L);
    }

    @Test
    void create() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(post(path)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1))
                .create(userDto);
    }

    @Test
    void change() throws Exception {
        when(userService.change(1L, userDto))
                .thenReturn(userDto);

        mockMvc.perform(patch(pathId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1))
                .change(1L, userDto);
    }

    @Test
    void remove_ok() throws Exception {
        //Act
        mockMvc.perform(delete(pathId))
                .andExpect(status().isOk());
        //Assert
        verify(userService).remove(1L);
    }

}