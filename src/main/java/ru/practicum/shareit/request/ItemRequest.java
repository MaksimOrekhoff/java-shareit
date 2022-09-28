package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequest {
    private final long id;
    private final String description;
    private final long requester;
    private final LocalDateTime created;
}
