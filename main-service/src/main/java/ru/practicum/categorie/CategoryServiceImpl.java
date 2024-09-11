package ru.practicum.categorie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.pagination.PaginationHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private CategoryEntity categoryExistCheck(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category not found"));
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        CategoryEntity entity = categoryRepository.save(CategoryMapper.mapToCategoryEntity(newCategoryDto));
        return CategoryMapper.mapToCategoryDto(entity);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        PaginationHelper<CategoryEntity> paginationHelper = new PaginationHelper<>(from, size);
        List<CategoryEntity> entities = paginationHelper.findAllWithPagination(categoryRepository::findAll);
        return CategoryMapper.mapToCategoryDto(entities);
    }

    @Override
    public CategoryDto getById(Long catId) {
        return CategoryMapper.mapToCategoryDto(categoryExistCheck(catId));
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        CategoryEntity entity = categoryExistCheck(catId);
        if (newCategoryDto.getName() != null) {
            entity.setName(newCategoryDto.getName());
        }
        CategoryEntity updatedEntity = categoryRepository.save(entity);
        return CategoryMapper.mapToCategoryDto(updatedEntity);
    }

    @Override
    public void delete(Long catId) {
        categoryRepository.deleteById(catId);
    }
}
