package ru.practicum.request.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.request.model.RequestStatus;

import java.util.Set;
@ToString
@Getter
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
