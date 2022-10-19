package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    UserDto booker1;
    UserDto owner;
    ItemDto itemDto;
    ItemRequestDto itemRequestDto;
    ItemDtoBooking itemDtoBookingOwner;
    ItemDtoBooking itemDtoBooking;
    BookingDtoItem lastBooking;
    BookingDtoItem nextBooking;
    CommentDto commentDto;
    String path = "/items";
    String pathId = "/items/1";

    @BeforeEach
    void startParam() {
        booker1 = new UserDto(
                1L,
                "John",
                "johndoe@email.com");
        itemRequestDto = new ItemRequestDto(1L,
                "create item",
                LocalDateTime.now().plusHours(1).withNano(0));
        owner = new UserDto(2L,
                "Nil",
                "nildoe@email.com");
        itemDto = new ItemDto(1L,
                "newItem",
                "item",
                true,
                1L);
        commentDto = new CommentDto(1L,
                "newComment",
                "John",
                LocalDateTime.now().plusMonths(20).withNano(0));
        List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto);
        lastBooking = new BookingDtoItem(1L, 1L);
        nextBooking = new BookingDtoItem(2L, 3L);
        itemDtoBookingOwner = new ItemDtoBooking(1L,
                "newItem",
                "item",
                true,
                lastBooking,
                nextBooking,
                comments);
        itemDtoBooking = new ItemDtoBooking(1L,
                "newItem",
                "item",
                true,
                null,
                null,
                comments);
    }

    @Test
    void getItemOwner() throws Exception {
        when(itemService.getItem(itemDto.getId(), owner.getId()))
                .thenReturn(itemDtoBookingOwner);

        mockMvc.perform(get(pathId)
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(itemDtoBookingOwner))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBookingOwner.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoBookingOwner.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoBookingOwner.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoBookingOwner.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoBookingOwner.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.comments[0].id", is(itemDtoBookingOwner.getComments().get(0).getId().intValue())));

        verify(itemService, times(1))
                .getItem(itemDto.getId(), owner.getId());
    }

    @Test
    void getItemNotOwner() throws Exception {
        when(itemService.getItem(itemDto.getId(), booker1.getId()))
                .thenReturn(itemDtoBooking);

        mockMvc.perform(get(pathId)
                        .header("X-Sharer-User-Id", booker1.getId())
                        .content(mapper.writeValueAsString(itemDtoBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoBooking.getAvailable())))
                .andExpect(jsonPath("$.comments[0].id", is(itemDtoBooking.getComments().get(0).getId().intValue())));

        verify(itemService, times(1))
                .getItem(itemDto.getId(), booker1.getId());
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems("item"))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get(path + "/search")
                        .param("text", "item")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1))
                .searchItems("item");
    }

    @Test
    void getAllItemsUser() throws Exception {
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());
        when(itemService.getAllItemsUser(owner.getId(), myPageRequest))
                .thenReturn(Collections.singletonList(itemDtoBooking));

        mockMvc.perform(get(path)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(itemDtoBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$[0].available", is(itemDtoBooking.getAvailable())))
                .andExpect(jsonPath("$[0].description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDtoBooking.getComments().get(0).getId().intValue())));

        verify(itemService, times(1))
                .getAllItemsUser(owner.getId(), myPageRequest);
    }

    @Test
    void add() throws Exception {
        when(itemService.addNewItem(owner.getId(), itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(post(path)
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1))
                .addNewItem(owner.getId(), itemDto);
    }

    @Test
    void update() throws Exception {
        when(itemService.update(owner.getId(), itemDto.getId(), itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(patch(pathId)
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1))
                .update(owner.getId(), itemDto.getId(), itemDto);
    }

    @Test
    void testUpdate() throws Exception {
        when(itemService.addComment(booker1.getId(), itemDto.getId(), commentDto))
                .thenReturn(commentDto);

        mockMvc.perform(post(pathId + "/comment")
                        .header("X-Sharer-User-Id", booker1.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(itemService, times(1))
                .addComment(booker1.getId(), itemDto.getId(), commentDto);
    }
}