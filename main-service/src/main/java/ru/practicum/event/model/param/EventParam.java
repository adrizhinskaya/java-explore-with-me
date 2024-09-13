package ru.practicum.event.model.param;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public class EventParam {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private int from;
    private int size;
}
