package ru.practicum.categorie;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
    private final CategoryMapper mapper;

    private CategoryEntity categoryExistCheck(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category not found"));
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        CategoryEntity entity = categoryRepository.save(mapper.toCategoryEntity(newCategoryDto));
        return mapper.toCategoryDto(entity);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        List<CategoryEntity> entities = getAllWithPagination(from, size, null);
        return mapper.toCategoryDto(entities);
    }

    @Override
    public CategoryDto getById(Long catId) {
        return mapper.toCategoryDto(categoryExistCheck(catId));
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        CategoryEntity entity = categoryExistCheck(catId);
        mapper.update(newCategoryDto, entity);
        CategoryEntity updatedEntity = categoryRepository.save(entity);
        return mapper.toCategoryDto(updatedEntity);
    }

    @Override
    public void delete(Long catId) {
        categoryRepository.deleteById(catId);
    }

    private List<CategoryEntity> getAllWithPagination(int from, int size, Sort sort) {
        PaginationHelper<CategoryEntity> ph = new PaginationHelper<>(from, size);
        Page<CategoryEntity> firstPage = categoryRepository.findAll(ph.getPageRequestForFirstPage(sort));
        Page<CategoryEntity> nextPage = firstPage.hasNext() ?
                categoryRepository.findAll(ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }
}
