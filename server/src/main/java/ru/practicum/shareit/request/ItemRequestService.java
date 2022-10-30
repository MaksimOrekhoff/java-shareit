package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestUserDto> findAllItemsRequests(Long userId, Integer from, Integer size);

    ItemRequestUserDto findById(Long itemId, Long userId);

    List<ItemRequestUserDto> findAllItemRequestUser(Long userId);
}
