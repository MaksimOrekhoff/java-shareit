package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    UserDto userDto;
    ItemRequestDto itemRequestDto;
    ItemRequestUserDto itemRequestUserDto;
    String path = "/requests";
    String pathId = "/requests/1";

    @BeforeEach
    void startParam() {
        userDto = new UserDto(
                1L,
                "John",
                "johndoe@email.com");
        itemRequestDto = new ItemRequestDto(1L,
                "newItemRequest",
                LocalDateTime.now().plusHours(3).withNano(0));
        itemRequestUserDto = new ItemRequestUserDto(1L, "newItemRequest",
                LocalDateTime.now().plusHours(3).withNano(0),
                new ArrayList<>());
    }

    @Test
    void getAllItemsRequests() throws Exception {
        when(itemRequestService.findAllItemsRequests(userDto.getId(), 0, 10))
                .thenReturn(Collections.singletonList(itemRequestUserDto));


        mockMvc.perform(get(path + "/all")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(itemRequestUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestUserDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestUserDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(itemRequestUserDto.getItems())));


        verify(itemRequestService, times(1))
                .findAllItemsRequests(userDto.getId(), 0, 10);
    }

    @Test
    void getAllItemsRequestsUser() throws Exception {
        when(itemRequestService.findAllItemRequestUser(userDto.getId()))
                .thenReturn(Collections.singletonList(itemRequestUserDto));

        mockMvc.perform(get(path)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestUserDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestUserDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(itemRequestUserDto.getItems())));


        verify(itemRequestService, times(1))
                .findAllItemRequestUser(userDto.getId());
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.findById(itemRequestUserDto.getId(), userDto.getId()))
                .thenReturn(itemRequestUserDto);

        mockMvc.perform(get(pathId)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestUserDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestUserDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(itemRequestUserDto.getItems())));


        verify(itemRequestService, times(1))
                .findById(itemRequestUserDto.getId(), userDto.getId());
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(itemRequestDto, userDto.getId()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post(path)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));

        verify(itemRequestService, times(1))
                .create(itemRequestDto, userDto.getId());
    }
}