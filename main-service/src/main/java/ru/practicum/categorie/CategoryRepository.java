package ru.practicum.categorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categorie.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
