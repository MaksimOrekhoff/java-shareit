package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.StatusItem;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.user.dto.UserDtoUp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoGet {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusItem status;
    private UserDtoUp booker;
    private ItemDtoUp item;
}
