package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private List<CommentDto> comments;
}
