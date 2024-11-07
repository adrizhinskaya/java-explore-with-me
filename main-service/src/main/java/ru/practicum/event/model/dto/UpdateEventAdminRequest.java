package ru.practicum.event.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import ru.practicum.event.model.enums.AdminStateAction;
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

    public void setEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationBadRequestException("EventDate must be at least 1 hour from now");
        }
        this.eventDate = eventDate;
    }
}
