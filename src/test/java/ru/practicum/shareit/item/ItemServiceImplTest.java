package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserRepository userDB;
    private final BookingRepository bookingRepository;
    ItemDto itemDto1;
    ItemDto itemDto2;
    User requester;
    User ownerItem1;
    User ownerItem2;

    @BeforeEach
    void start() {
        requester = new User(1L, "John", "john.doe@example.com");
        itemDto1 = new ItemDto(1L,
                "item",
                "notebook",
                true,
                null);

        itemDto2 = new ItemDto(2L,
                "newItem",
                "phone",
                false,
                1L);

        ownerItem1 = new User(2L, "Jane", "jane.doe@example.com");
        ownerItem2 = new User(3L, "Bob", "bob.doe@example.com");
    }

    @Test
    void addNewItemWithoutUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.addNewItem(100L, itemDto1));

        Assertions.assertEquals("Такой пользователь не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void addNewItemWithoutRequest() {
        userDB.save(requester);
        userDB.save(ownerItem1);
        ItemDto item = itemService.addNewItem(ownerItem1.getId(), itemDto1);

        assertNotNull(item);
        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getAvailable(), itemDto1.getAvailable());
        assertEquals(item.getDescription(), itemDto1.getDescription());
        assertEquals(item.getId(), itemDto1.getId());
        assertNull(item.getRequestId());
    }

    @Test
    void updateWithoutUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.update(100L, itemDto1.getId(), itemDto1));

        Assertions.assertEquals("Такой пользователь не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void updateWithoutItem() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            ItemDto newItem = new ItemDto(100L,
                    "three",
                    "new", true, null);
            itemService.update(1L, newItem.getId(), newItem);
        });

        Assertions.assertEquals("Такая вещь не сущетсвует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void updateWithoutAccess() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.update(1L, itemDto1.getId(), itemDto1));

        Assertions.assertEquals("Это не ваша вещь.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void addUpdate() {
        ItemDto itemDto = new ItemDto(1L, "newName",
                "notebook", true, null);
        ItemDto item = itemService.update(2L, 1L, itemDto);
        assertNotNull(item);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void getItemWithoutItem() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.getItem(10L, requester.getId()));

        Assertions.assertEquals("Такая вещь не сущетсвует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getItemUser() {
        ItemDtoBooking itemDto = itemService.getItem(itemDto1.getId(), requester.getId());

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getItemOwnerEmptyBooking() {
        ItemDtoBooking itemDto = itemService.getItem(itemDto1.getId(), ownerItem1.getId());

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getItemOwnerBooking() {
        bookingRepository.deleteById(1L);
        ItemDtoBooking itemDto = itemService.getItem(itemDto2.getId(), ownerItem2.getId());

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), itemDto2.getName());
        assertEquals(itemDto.getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDto.getDescription(), itemDto2.getDescription());
        assertNotNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void getAllItemsUser() {
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());

        List<ItemDtoBooking> itemDtoBookings = (List<ItemDtoBooking>) itemService.getAllItemsUser(ownerItem2.getId(), myPageRequest);

        assertFalse(itemDtoBookings.isEmpty());
        assertEquals(itemDtoBookings.get(0).getName(), itemDto2.getName());
        assertEquals(itemDtoBookings.get(0).getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDtoBookings.get(0).getDescription(), itemDto2.getDescription());
        assertNull(itemDtoBookings.get(0).getLastBooking());
        assertNotNull(itemDtoBookings.get(0).getNextBooking());
    }

    @Test
    void searchItemsEmpty() {
        Collection<ItemDto> itemDtos = itemService.searchItems("");

        assertTrue(itemDtos.isEmpty());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void searchItems() {
        List<ItemDto> itemDtos = (List<ItemDto>) itemService.searchItems("tem");

        ItemDto itemDto = itemDtos.get(0);
        assertFalse(itemDtos.isEmpty());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getRequestId(), itemDto1.getRequestId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getId(), itemDto1.getId());
    }

    @Test
    void addCommentNotItem() {
        CommentDto commentDto = new CommentDto(1L, "good", "Jane", LocalDateTime.now());
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.addComment(100L, 2L, commentDto));

        Assertions.assertEquals("Такая вещь не сущетсвует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void addCommentNotUser() {
        CommentDto commentDto = new CommentDto(1L, "good", "Jane", LocalDateTime.now());
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> itemService.addComment(100L, 2L, commentDto));

        Assertions.assertEquals("Такой пользователь не сущетсвует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = {"after.sql"}, executionPhase = AFTER_TEST_METHOD)
    })
    void addCommentCurrentBooking() {
        CommentDto commentDto = new CommentDto(1L, "good", "Jane", LocalDateTime.now());
        NotAvailableBooking thrown = Assertions.assertThrows(NotAvailableBooking.class, () -> itemService.addComment(2L, 2L, commentDto));

        Assertions.assertEquals("Бронирование не окончено.", thrown.getMessage());
    }
}