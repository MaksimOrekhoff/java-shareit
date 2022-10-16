package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUp;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    UserDto booker1;
    UserDto owner;
    ItemDtoUp itemDtoUp;
    BookingDtoGet bookingDtoGet;
    BookingDto bookingDto;
    UserDtoUp userDtoUp;

    @BeforeEach
    void startParam() {
        userDtoUp = new UserDtoUp(1L);
        itemDtoUp = new ItemDtoUp(1L, "item");
        booker1 = new UserDto(
                1L,
                "John",
                "johndoe@email.com");
        owner = new UserDto(
                2L,
                "Joh",
                "johdoe@email.com");
        bookingDtoGet = new BookingDtoGet(1L,
                LocalDateTime.now().plusMonths(19).withNano(0),
                LocalDateTime.now().plusMonths(20).withNano(0),
                StatusItem.WAITING,
                userDtoUp,
                itemDtoUp);
        bookingDto = new BookingDto(1L,
                1L,
                LocalDateTime.now().plusMonths(19).withNano(0),
                LocalDateTime.now().plusMonths(20).withNano(0),
                StatusItem.WAITING);
    }

    @Test
    void add() throws Exception {
        when(bookingService.addNewBooking(1L, bookingDto))
                .thenReturn(bookingDtoGet);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoGet.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoGet.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoGet.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoGet.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .addNewBooking(1L, bookingDto);
    }

    @Test
    void changeBooking() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), eq(true)))
                .thenReturn(bookingDtoGet);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDtoGet))
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoGet.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoGet.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoGet.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoGet.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .update(anyLong(), anyLong(), eq(true));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.findBooking(owner.getId(), bookingDto.getId()))
                .thenReturn(bookingDtoGet);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(bookingDtoGet))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoGet.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoGet.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoGet.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoGet.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .findBooking(owner.getId(), bookingDto.getId());
    }

    @Test
    void getAllBooking() throws Exception {
        when(bookingService.findAllBooking(booker1.getId(), "ALL", 0, 10))
                .thenReturn(Collections.singletonList(bookingDtoGet));

        mockMvc.perform(get("/bookings?from=0&size=10")
                        .header("X-Sharer-User-Id", booker1.getId())
                        .content(mapper.writeValueAsString(bookingDtoGet))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoGet.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtoGet.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoGet.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoGet.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .findAllBooking(booker1.getId(), "ALL", 0, 10);
    }

    @Test
    void getBookingOwner() throws Exception {
        when(bookingService.findBookingOwner(owner.getId(), "ALL", 0, 10))
                .thenReturn(Collections.singletonList(bookingDtoGet));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(bookingDtoGet))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoGet.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtoGet.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoGet.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoGet.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .findBookingOwner(owner.getId(), "ALL", 0, 10);
    }

}