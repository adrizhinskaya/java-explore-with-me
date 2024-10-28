package ru.practicum.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.user.model.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByIdIn(Set<Long> ids, Pageable pageable);
    Optional<UserEntity> findFirstByEmail(String name);
}

