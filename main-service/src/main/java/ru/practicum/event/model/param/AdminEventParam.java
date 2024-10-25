package ru.practicum.event.model.param;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.events.Event;
import ru.practicum.event.model.EventState;

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
