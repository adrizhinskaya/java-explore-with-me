package ru.practicum.event.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.event.model.AdminStateAction;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class UpdateEventAdminRequest extends UpdateEventRequest {
    @Enumerated(EnumType.STRING)
    private AdminStateAction stateAction;

    @Override
    @AssertTrue(message = "EventDate cannot be earlier than 1 hours from now")
    protected Boolean isEventDateCorrect() {
        if(eventDate != null) {
            return eventDate.isAfter(LocalDateTime.now().plusHours(1));
        }
        return true;
    }
}
