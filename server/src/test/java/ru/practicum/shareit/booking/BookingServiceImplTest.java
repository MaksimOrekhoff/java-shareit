package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@Transactional
class BookingServiceImplTest {
    private final BookingService bookingService;
    BookingDto bookingDto;

    @BeforeEach
    void start() {
        bookingDto = new BookingDto(1L, 1L,
                LocalDateTime.now().plusMonths(5).withNano(0),
                LocalDateTime.now().plusMonths(6).withNano(0),
                StatusItem.WAITING);
    }


    @Test
    void addNewBookingWithoutUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> bookingService.addNewBooking(100L, new BookingDto()));

        Assertions.assertEquals("Такой пользователь не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void addNewBookingWithoutItem() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingDto.setItemId(1000L);
            bookingService.addNewBooking(1L, bookingDto);
        });

        Assertions.assertEquals("Такая вещь не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void addNewBookingNotAvailable() {
        NotAvailableBooking thrown = Assertions.assertThrows(NotAvailableBooking.class, () -> {
            bookingDto.setItemId(2L);
            bookingService.addNewBooking(1L, bookingDto);
        });

        Assertions.assertEquals("Не доступно для бронирования.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void addNewBookingOwner() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingDto.setItemId(1L);
            bookingService.addNewBooking(2L, bookingDto);
        });

        Assertions.assertEquals("Для Вас не доступна.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void addNewBookingFalseTime() {
        NotAvailableBooking thrown = Assertions.assertThrows(NotAvailableBooking.class, () -> {
            bookingDto.setEnd(LocalDateTime.now());
            bookingDto.setStart(LocalDateTime.now().plusMonths(1));
            bookingService.addNewBooking(1L, bookingDto);
        });

        Assertions.assertEquals("Окончание бронирование раньше начала.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"beforeAddBook.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void addNewBooking() {
        BookingDto newBooking = new BookingDto(1L, 1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                StatusItem.WAITING);

        BookingDtoGet booking = bookingService.addNewBooking(1L, newBooking);

        assertEquals(booking.getBooker().getId(), 1L);
        assertEquals(booking.getStatus(), StatusItem.WAITING);
        assertEquals(booking.getItem().getId(), 1L);
    }

    @Test
    void updateNotBooking() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingDto.setEnd(LocalDateTime.now());
            bookingDto.setStart(LocalDateTime.now().plusMonths(1));
            bookingService.update(1L, 1L, true);
        });

        Assertions.assertEquals("Такого бронирования не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void updateBookingStatus() {
        NotAvailableBooking thrown = Assertions.assertThrows(NotAvailableBooking.class, () -> bookingService.update(3L, 2L, true));

        Assertions.assertEquals("Статус уже был подтвержден.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void updateBookingNotAva() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> bookingService.update(1L, 2L, true));

        Assertions.assertEquals("Нет прав.", thrown.getMessage());
    }

    @Test
    void findBookingNotBooking() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.findBooking(1L, 10L);
        });

        Assertions.assertEquals("Такого бронирования не существует.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void updateBookingTrue() {
        BookingDtoGet booking = bookingService.update(3L, 1L, true);

        assertEquals(booking.getBooker().getId(), 2L);
        assertEquals(booking.getStatus(), StatusItem.APPROVED);
        assertEquals(booking.getItem().getId(), 2L);

    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void updateBookingFalse() {
        BookingDtoGet booking = bookingService.update(3L, 1L, false);

        assertEquals(booking.getBooker().getId(), 2L);
        assertEquals(booking.getStatus(), StatusItem.REJECTED);
        assertEquals(booking.getItem().getId(), 2L);

    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingNotAccess() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> bookingService.findBooking(1L, 1L));

        Assertions.assertEquals("Нет прав.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBooking() {
        BookingDtoGet booking = bookingService.findBooking(2L, 1L);

        assertEquals(booking.getBooker().getId(), 2L);
        assertEquals(booking.getStatus(), StatusItem.WAITING);
        assertEquals(booking.getItem().getId(), 2L);
    }

    @Test
    void findAllBookingNotUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> bookingService.findAllBooking(100L, "ALL", 0, 10));
        ErrorResponse errorResponse = new ErrorResponse(thrown.getMessage());
        Assertions.assertEquals("Такой пользователь не существует.", errorResponse.getError());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingAll() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "ALL", 0, 10);

        assertEquals(booking.size(), 2);
        assertEquals(booking.get(0).getId(), 2L);
        assertEquals(booking.get(0).getStatus(), StatusItem.APPROVED);
        assertEquals(booking.get(0).getItem().getId(), 2L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingAllFromNot0() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "ALL", 1, 10);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), 1L);
        assertEquals(booking.get(0).getStatus(), StatusItem.WAITING);
        assertEquals(booking.get(0).getItem().getId(), 2L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingFuture() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "FUTURE", 0, 10);

        assertEquals(booking.size(), 2);
        assertEquals(booking.get(0).getId(), 2L);
        assertEquals(booking.get(0).getStatus(), StatusItem.APPROVED);
        assertEquals(booking.get(0).getItem().getId(), 2L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingCurrent() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "CURRENT", 0, 10);

        assertEquals(booking.size(), 0);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingPast() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "PAST", 0, 10);

        assertEquals(booking.size(), 0);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingWaiting() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "WAITING", 0, 10);

        assertEquals(booking.size(), 1);
        assertEquals(booking.get(0).getId(), 1L);
        assertEquals(booking.get(0).getStatus(), StatusItem.WAITING);
        assertEquals(booking.get(0).getItem().getId(), 2L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingRejected() {
        List<BookingDtoGet> booking = bookingService.findAllBooking(2L, "REJECTED", 0, 10);

        assertEquals(booking.size(), 0);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingOwnerWithoutItem() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> bookingService.findBookingOwner(4L, "any", 0, 10));

        Assertions.assertEquals("У пользователя нет вещей.", thrown.getMessage());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    })
    void findBookingOwner() {
        List<BookingDtoGet> booking = bookingService.findBookingOwner(3L, "ALL", 0, 10);

        assertEquals(booking.size(), 2);
        assertEquals(booking.get(0).getId(), 2L);
        assertEquals(booking.get(0).getStatus(), StatusItem.APPROVED);
        assertEquals(booking.get(0).getItem().getId(), 2L);
    }
}