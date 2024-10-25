package ru.practicum.categorie.model;

import org.mapstruct.*;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;

import java.util.List;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CategoryMapper {
    public abstract CategoryEntity toCategoryEntity(NewCategoryDto newCategoryDto);

    public abstract CategoryDto toCategoryDto(CategoryEntity categoryEntity);

    public abstract List<CategoryDto> toCategoryDto(Iterable<CategoryEntity> categoryEntities);

    public abstract void update(NewCategoryDto dto, @MappingTarget CategoryEntity model);
}