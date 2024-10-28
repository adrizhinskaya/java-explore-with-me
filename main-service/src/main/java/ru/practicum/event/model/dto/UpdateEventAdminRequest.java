package ru.practicum.event.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import ru.practicum.event.model.AdminStateAction;
import ru.practicum.exception.ValidationBadRequestException;

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
    public void setEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationBadRequestException("EventDate must be at least 1 hour from now");
        }
        this.eventDate = eventDate;
    }
}
