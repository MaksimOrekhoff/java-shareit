package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class Item {
    private final long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final long userId;
    // private final Long requestId;
}
