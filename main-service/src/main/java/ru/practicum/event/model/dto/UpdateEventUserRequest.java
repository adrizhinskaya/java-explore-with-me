package ru.practicum.event.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import ru.practicum.event.model.enums.UserStateAction;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {
    @Enumerated(EnumType.STRING)
    private UserStateAction stateAction;
}
