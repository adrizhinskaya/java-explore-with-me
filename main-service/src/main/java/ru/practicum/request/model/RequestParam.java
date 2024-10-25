package ru.practicum.request.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestParam {
    private Long userId;
    private Long eventId;
}