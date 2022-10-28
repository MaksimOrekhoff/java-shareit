package ru.practicum.shareit.request;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;

import java.time.LocalDateTime;

@Component
@Data
public class MapperItemRequest {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                userId,
                LocalDateTime.now());
    }

    public ItemRequestUserDto toItemRequestUserDto(ItemRequest itemRequest) {
        return new ItemRequestUserDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                null);
    }
}
