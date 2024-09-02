package ru.practicum.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public class UserShortDto {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    private String name;
}
