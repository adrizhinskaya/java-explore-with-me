package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class NewEventDto {
    @NotBlank(message = "Event annotation must not be blank")
    @Size(min = 20, message = "Event annotation name must be longer than 20 symbols")
    @Size(max = 2000, message = "Event annotation must be shorter than 2000 symbols")
    private String annotation;

    @NotNull(message = "Event category must not be null")
    private Long category;

    @NotBlank(message = "Event description must not be blank")
    @Size(min = 20, message = "Event description must be longer than 20 symbols")
    @Size(max = 7000, message = "Event description must be shorter than 7000 symbols")
    private String description;

    @NotNull(message = "Event eventDate must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;

    @NotNull(message = "Event location must not be null")
    private Location location;
    private Boolean paid;

    @PositiveOrZero(message = "Event participantLimit must be positive or zero")
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotBlank(message = "Event title must not be blank")
    @Size(min = 3, message = "Event title must be longer than 3 symbols")
    @Size(max = 120, message = "Event title must be shorter than 120 symbols")
    private String title;

    @AssertTrue(message = "EventDate cannot be earlier than 2 hours from now")
    private Boolean isEventDateAfter2HoursFromNow() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
