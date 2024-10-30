package ru.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.request.model.enums.RequestStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(of = {"id"})
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
