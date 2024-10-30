package ru.practicum.compilation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UpdateCompilationRequest {
    @Size(min = 1, message = "{validation.annotation.size.too_short}")
    @Size(max = 50, message = "{validation.annotation.size.too_long}")
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
