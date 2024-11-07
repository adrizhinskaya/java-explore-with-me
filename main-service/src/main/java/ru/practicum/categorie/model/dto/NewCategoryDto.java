package ru.practicum.categorie.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCategoryDto {
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 1, message = "Category name must be longer than 1 symbol")
    @Size(max = 50, message = "Category name must be shorter than 50 symbols")
    private String name;
}
