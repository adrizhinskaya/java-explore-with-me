package ru.practicum.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.UserEntity;

import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByIdIn(Set<Long> ids, Pageable pageable);
}

