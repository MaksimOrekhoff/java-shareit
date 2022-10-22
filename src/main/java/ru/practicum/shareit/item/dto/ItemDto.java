package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {
    private final long id;
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String description;
    @NotNull
    private final Boolean available;
    private final Long requestId;
}
