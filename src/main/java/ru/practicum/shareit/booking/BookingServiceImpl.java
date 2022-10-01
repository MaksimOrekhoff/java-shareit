package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;
import ru.practicum.shareit.exceptions.InvalidStatusException;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoUp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MapperBooking mapperBooking;

    @Override
    public BookingDto addNewBooking(Long userId, BookingDto bookingDto) {
        validation(userId, bookingDto);
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus().equals(StatusItem.APPROVED))
                .filter(booking -> Objects.equals(booking.getItemId(), bookingDto.getItemId()))
                .filter(booking -> booking.getStart().isAfter(bookingDto.getStart()) && booking.getEnd().isBefore(bookingDto.getStart())
                        || booking.getStart().isAfter(bookingDto.getEnd()) && booking.getEnd().isBefore(bookingDto.getEnd()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            Booking booking = mapperBooking.toBooking(bookingDto);
            booking.setBooker(userId);
            booking.setStatus(StatusItem.WAITING);
            Booking booking1 = bookingRepository.save(booking);
            return mapperBooking.toBookingDto(booking1);
        }
        throw new NotAvailableBooking("Данный период не доступен для бронирования.");
    }

    @Override
    public BookingDtoUpdate update(Long userId, Long bookingId, Boolean approved) {
        validUpdate(userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        if (approved) {
            booking.setStatus(StatusItem.APPROVED);

        } else {
            booking.setStatus(StatusItem.REJECTED);
        }
        Booking booking1 = bookingRepository.save(booking);
        BookingDtoUpdate bookingDtoApp = mapperBooking.toBookingDtoApp(booking1);
        Item item = itemRepository.findById(booking1.getItemId()).get();

        bookingDtoApp.setItem(new ItemDtoUp(item.getId(), item.getName()));
        bookingDtoApp.setBooker(new UserDtoUp(booking1.getBooker()));
        return bookingDtoApp;
    }

    @Override
    public BookingDtoGet findBooking(Long userId, Long bookingId) {

        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Такого бронирования не существует.");
        }
        Long itemId = booking.get().getItemId();
        Item item = itemRepository.findById(itemId).get();
        if (!(Objects.equals(userId, booking.get().getBooker()) ||
                Objects.equals(item.getUserId(), userId))) {
            throw new NotFoundException("Нет прав.");
        }
        BookingDtoGet bookingDtoGet = mapperBooking.bookingDtoGet(booking.get());
        bookingDtoGet.setItem(new ItemDtoUp(item.getId(), item.getName()));
        bookingDtoGet.setBooker(new UserDtoUp(booking.get().getBooker()));
        return bookingDtoGet;
    }


    @Override
    public List<BookingDtoGet> findAllBooking(Long userId, String state) {
        validGet(userId);
        return sendBookingDto(stateAll(userId), state);
    }

    private List<BookingDtoGet> sendBookingDto(List<BookingDtoGet> bookingDtoGet, String state) {
        switch (state) {
            case "ALL":
                return bookingDtoGet;
            case "CURRENT":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getEnd().isAfter(LocalDateTime.now())
                                && bookingDtoGt.getStart().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStatus().equals(StatusItem.WAITING))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStatus().equals(StatusItem.REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new InvalidStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoGet> findBookingOwner(Long userId, String state) {
        List<Long> ids = itemRepository.findAll().stream().map(Item::getUserId)
                .collect(Collectors.toList());
        if (!ids.contains(userId)) {
            throw new NotFoundException("У пользователя нет вещей.");
        }
        List<Long> id = itemRepository.findAll().stream()
                .filter(item -> item.getUserId() == userId)
                .map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> id.contains(booking.getItemId()))
                .sorted(new BookingComparator())
                .collect(Collectors.toList());

        List<BookingDtoGet> bookingDtoGets = getBookingDtoGets(bookings);
        return sendBookingDto(bookingDtoGets, state);
    }

    private List<BookingDtoGet> getBookingDtoGets(List<Booking> bookings) {
        List<BookingDtoGet> bookingDtoGets = new ArrayList<>();
        for (Booking b : bookings) {
            BookingDtoGet bookingDtoGet = mapperBooking.bookingDtoGet(b);
            Optional<Booking> booking = bookingRepository.findById(b.getId());
            Long itemId = booking.get().getItemId();
            Item item = itemRepository.findById(itemId).get();
            bookingDtoGet.setItem(new ItemDtoUp(item.getId(), item.getName()));
            bookingDtoGet.setBooker(new UserDtoUp(booking.get().getBooker()));
            bookingDtoGets.add(bookingDtoGet);
        }
        return bookingDtoGets;
    }


    private List<BookingDtoGet> stateAll(Long userId) {
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> Objects.equals(booking.getBooker(), userId))
                .sorted(new BookingComparator())
                .collect(Collectors.toList());
        return getBookingDtoGets(bookings);
    }

    private void validation(Long userId, BookingDto bookingDto) {
        validGet(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Такая вещь не существует.");
        }
        if (!item.get().getAvailable()) {
            throw new NotAvailableBooking("Не доступно для бронирования.");
        }
        if (item.get().getUserId() == userId) {

            throw new NotFoundException("Для Вас не доступна.");

        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotAvailableBooking("Окончание бронирование раньше начала.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new NotAvailableBooking("Начало бронирования в прошлом.");
        }
    }

    private void validUpdate(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Такого бронирования не существует.");
        }
        Optional<Item> item = itemRepository.findById(booking.get().getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Такая вешь не существует.");
        }
        if (item.get().getUserId() != userId) {
            throw new NotFoundException("Нет прав.");
        }
        if (booking.get().getStatus().equals(StatusItem.APPROVED)) {
            throw new NotAvailableBooking("Статус уже был подтвержден.");
        }
    }

    private void validGet(Long userId) {
        List<Long> ids = userRepository.findAll().stream().map(User::getId)
                .collect(Collectors.toList());
        if (!ids.contains(userId)) {
            throw new NotFoundException("Такой пользователь не существует.");
        }
    }

    public static class BookingComparator implements Comparator<Booking> {
        public int compare(Booking a, Booking b) {
            if (a.getStart().equals(b.getStart())) {
                return 0;
            } else if (a.getStart().isBefore(b.getStart())) {
                return 1;
            }
            return -1;
        }
    }
}
