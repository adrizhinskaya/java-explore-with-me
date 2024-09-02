package ru.practicum.categorie.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public class CategoryDto {
    private Long id;
    @Size(min = 1, message = "{validation.annotation.size.too_short}")
    @Size(max = 50, message = "{validation.annotation.size.too_long}")
    private String name;
}
