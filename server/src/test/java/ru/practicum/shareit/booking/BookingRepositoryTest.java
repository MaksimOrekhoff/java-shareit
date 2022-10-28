package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class BookingRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findAllByBooker() {
        User owner = userRepository.save(new User(1L, "owner", "owner@mail.com"));
        Item item = itemRepository.save(new Item(1L, "item1", "отвертка", true, owner.getId(), null));
        User booker = userRepository.save(new User(2L, "booker", "booker@mail.com"));
        Booking booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                item.getId(),
                booker.getId(),
                StatusItem.WAITING));

        List<Booking> result = bookingRepository.findAllByBooker(booker.getId(),
                new MyPageRequest(0, 10, Sort.by("start").descending()));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker(), result.get(0).getBooker());
        assertEquals(booking.getItemId(), result.get(0).getItemId());
        assertEquals(booker.getId(), result.get(0).getBooker());
        assertEquals(booking.getEnd(), result.get(0).getEnd());
        assertEquals(booking.getStart(), result.get(0).getStart());
    }
}