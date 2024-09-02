package ru.practicum.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewUserDto {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String email;
}
