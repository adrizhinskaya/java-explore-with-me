package ru.practicum.compilation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.event.model.dto.EventShortDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class CompilationDto {
    @NotNull
    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private Boolean pinned;
    private List<EventShortDto> events;
}
