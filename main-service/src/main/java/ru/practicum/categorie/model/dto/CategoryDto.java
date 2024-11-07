package ru.practicum.categorie.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString(of = {"id"})
public class CategoryDto {
    private Long id;
    private String name;
}
