package ru.practicum.event.model.param;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaginationEventParam {
    private Integer from;
    private Integer size;
}
