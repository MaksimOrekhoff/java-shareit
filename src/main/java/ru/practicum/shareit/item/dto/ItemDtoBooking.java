package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoBooking {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private List<CommentDto> comments;
}
