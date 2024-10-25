package ru.practicum.request.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import ru.practicum.request.model.RequestStatus;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
