package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class MyPageRequest extends PageRequest {
    int offset;

    public MyPageRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
