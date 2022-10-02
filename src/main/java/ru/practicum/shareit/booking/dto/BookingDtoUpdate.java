package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.StatusItem;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.user.dto.UserDtoUp;

@Data
@AllArgsConstructor
public class BookingDtoUpdate {
    private Long id;
    private StatusItem status;
    private UserDtoUp booker;
    private ItemDtoUp item;
}
