package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.exception.ValidationBadRequestException;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public abstract class UpdateEventRequest {
    @Size(min = 20, message = "Event annotation name must be longer than 20 symbols")
    @Size(max = 2000, message = "Event annotation must be shorter than 2000 symbols")
    protected String annotation;

    protected Long category;

    @Size(min = 20, message = "Event description name must be longer than 20 symbols")
    @Size(max = 7000, message = "Event description must be shorter than 7000 symbols")
    protected String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime eventDate;

    protected Location location;

    protected Boolean paid;

    @Positive(message = "Event participantLimit must be positive")
    protected Integer participantLimit;

    protected Boolean requestModeration;

    @Size(min = 3, message = "Event title name must be longer than 3 symbols")
    @Size(max = 120, message = "Event title must be shorter than 120 symbols")
    protected String title;
}
