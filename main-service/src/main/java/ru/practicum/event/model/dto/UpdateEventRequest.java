package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@Getter
public abstract class UpdateEventRequest {
    @Size(min = 20, message = "{validation.annotation.size.too_short}")
    @Size(max = 2000, message = "{validation.annotation.size.too_long}")
    protected String annotation;
    protected Long category;
    @Size(min = 20, message = "{validation.annotation.size.too_short}")
    @Size(max = 7000, message = "{validation.annotation.size.too_long}")
    protected String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime eventDate;
    protected Location location;
    protected Boolean paid;
    protected Integer participantLimit;
    protected Boolean requestModeration;
    @Size(min = 3, message = "{validation.annotation.size.too_short}")
    @Size(max = 120, message = "{validation.annotation.size.too_long}")
    protected String title;

    @AssertTrue(message = "EventDate cannot be earlier than 2 hours from now")
    protected boolean isEventDateCorrect() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
