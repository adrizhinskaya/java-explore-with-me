package ru.practicum.compilation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.event.model.dto.EventShortDto;

import java.util.List;

@Builder
@Getter
@ToString
public class CompilationDto {
    @NotNull
    private Long id;
    private List<EventShortDto> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
