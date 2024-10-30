package ru.practicum.event.model.param;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class AdminEventParam {
    private Set<Long> users;
    private Set<EventState> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
}
