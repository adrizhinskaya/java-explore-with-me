package ru.practicum.event.model.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.location.Location;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class NewEventDto {
    @NotBlank
    @Size(min = 20, message = "{validation.annotation.size.too_short}")
    @Size(max = 2000, message = "{validation.annotation.size.too_long}")
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, message = "{validation.annotation.size.too_short}")
    @Size(max = 7000, message = "{validation.annotation.size.too_long}")
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, message = "{validation.annotation.size.too_short}")
    @Size(max = 120, message = "{validation.annotation.size.too_long}")
    private String title;

    @AssertTrue(message = "EventDate cannot be earlier than 2 hours from now")
    private boolean isEventDateAfter2HoursFromNow() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
