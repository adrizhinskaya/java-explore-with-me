package ru.practicum.categorie.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, message = "{validation.annotation.size.too_short}")
    @Size(max = 50, message = "{validation.annotation.size.too_long}")
    private String name;
}
