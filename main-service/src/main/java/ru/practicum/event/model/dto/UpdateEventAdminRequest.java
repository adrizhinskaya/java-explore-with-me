package ru.practicum.event.model.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import ru.practicum.event.model.AdminStateAction;

import java.time.LocalDateTime;

@Getter
public class UpdateEventAdminRequest extends UpdateEventRequest {
    private AdminStateAction stateAction;
    @Override
    @AssertTrue(message = "EventDate cannot be earlier than 1 hours from now")
    protected boolean isEventDateCorrect() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(1));
    }
}
