package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.MyPageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoUp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    public BookingDtoGet addNewBooking(Long userId, BookingDto bookingDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такой пользователь не существует."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Такая вещь не существует."));
        validation(userId, bookingDto);
        Booking booking = mapperBooking.toBooking(bookingDto, userId);
        log.info("Получен запрос на бронирование вещи с id {} от пользователя {}", bookingDto.getItemId(), userId);
        Booking newBooking = bookingRepository.save(booking);
        return mapperBooking.bookingDtoGet(newBooking,
                new UserDtoUp(booking.getBooker()),
                new ItemDtoUp(item.getId(), item.getName()));
    }

    @Override
    public BookingDtoGet update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования не существует."));
        Item item = itemRepository.findById(booking.getItemId()).get();
        validUpdate(item, booking, userId);
        StatusItem statusItem = approved ? StatusItem.APPROVED : StatusItem.REJECTED;
        booking.setStatus(statusItem);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Бронированию {} установлен статус {}", bookingId, statusItem);
        return mapperBooking.bookingDtoGet(newBooking,
                new UserDtoUp(newBooking.getBooker()),
                new ItemDtoUp(item.getId(), item.getName()));
    }

    @Override
    public BookingDtoGet findBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования не существует."));
        Item item = itemRepository.findById(booking.getItemId()).get();
        if (!(Objects.equals(userId, booking.getBooker()) ||
                Objects.equals(item.getUserId(), userId))) {
            throw new NotFoundException("Нет прав.");
        }
        log.info("Запрос на просмотр бронирования {} от пользователя {}", bookingId, userId);
        return mapperBooking.bookingDtoGet(booking,
                new UserDtoUp(booking.getBooker()),
                new ItemDtoUp(item.getId(), item.getName()));
    }


    @Override
    public List<BookingDtoGet> findAllBooking(Long userId, String state, Integer from, Integer size) {
        validGet(userId);
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.by("start").descending());
        log.info("Просмотр всех бронирований пользователя {}", userId);
        return sendBookingDto(stateAll(userId, pageRequest), state);
    }

    private List<BookingDtoGet> sendBookingDto(List<BookingDtoGet> bookingDtoGet, String state) {
        switch (state) {
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
                return bookingDtoGet;
        }
    }

    @Override
    public List<BookingDtoGet> findBookingOwner(Long userId, String state, Integer from, Integer size) {
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.by("start").descending());
        List<Long> id = itemRepository.findAll().stream()
                .filter(item -> item.getUserId() == userId)
                .map(Item::getId).collect(Collectors.toList());
        if (id.isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей.");
        }
        List<Booking> bookings = bookingRepository.findAll(pageRequest).stream()
                .filter(booking -> id.contains(booking.getItemId()))
                // .sorted(new BookingComparator())
                .collect(Collectors.toList());
        List<BookingDtoGet> bookingDtoGets = getBookingDtoGets(bookings);
        log.info("Просмотр всех бронирований владельца вещей {}", userId);
        return sendBookingDto(bookingDtoGets, state);
    }

    private List<BookingDtoGet> getBookingDtoGets(List<Booking> bookings) {
        List<BookingDtoGet> bookingDtoGets = new ArrayList<>();
        for (Booking booking : bookings) {
            Item item = itemRepository.findById(booking.getItemId()).get();
            BookingDtoGet bookingDtoGet = mapperBooking.bookingDtoGet(booking,
                    new UserDtoUp(booking.getBooker()),
                    new ItemDtoUp(item.getId(), item.getName()));
            bookingDtoGets.add(bookingDtoGet);
        }
        return bookingDtoGets;
    }


    private List<BookingDtoGet> stateAll(Long userId, PageRequest pageRequest) {
        List<Booking> bookings = bookingRepository.findAllByBooker(userId, pageRequest);
        return getBookingDtoGets(bookings);
    }

    private void validation(Long userId, BookingDto bookingDto) {
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus().equals(StatusItem.APPROVED))
                .filter(booking -> Objects.equals(booking.getItemId(), bookingDto.getItemId()))
                .filter(booking -> booking.getStart().isAfter(bookingDto.getStart())
                        && booking.getEnd().isBefore(bookingDto.getStart())
                        || booking.getStart().isAfter(bookingDto.getEnd())
                        && booking.getEnd().isBefore(bookingDto.getEnd()))
                .collect(Collectors.toList());
        if (!bookings.isEmpty()) {
            throw new NotAvailableBooking("Данный период не доступен для бронирования.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (!item.getAvailable()) {
            throw new NotAvailableBooking("Не доступно для бронирования.");
        }
        if (item.getUserId() == userId) {
            throw new NotFoundException("Для Вас не доступна.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotAvailableBooking("Окончание бронирование раньше начала.");
        }
    }

    private void validUpdate(Item item, Booking booking, Long userId) {
        if (item.getUserId() != userId) {
            throw new NotFoundException("Нет прав.");
        }
        if (booking.getStatus().equals(StatusItem.APPROVED)) {
            throw new NotAvailableBooking("Статус уже был подтвержден.");
        }
    }

    private void validGet(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такой пользователь не существует."));
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
