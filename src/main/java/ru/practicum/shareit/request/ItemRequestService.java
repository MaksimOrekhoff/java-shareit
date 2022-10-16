package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestUserDto> findAllItemsRequests(PageRequest pageRequest, Long userId);

    ItemRequestUserDto findById(Long itemId, Long userId);

    List<ItemRequestUserDto> findAllItemRequestUser(Long userId);
}
