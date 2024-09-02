package ru.practicum.event.model.dto;

import lombok.Getter;
import ru.practicum.event.model.UserStateAction;

@Getter
public class UpdateEventUserRequest extends UpdateEventRequest {
    private UserStateAction stateAction;
}
