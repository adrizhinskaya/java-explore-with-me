package ru.practicum.event.model.param;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EventUpdateParam {
    private Long userId;
    private Long eventId;
}
