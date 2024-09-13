package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.RequestEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    List<RequestEntity> findAllByIdIn(Set<Long> ids);
    Optional<RequestEntity> findByRequester_idAndEvent_id(Long userId, Long eventId);
    List<RequestEntity> findAllByRequester_id(Long userId);
    List<RequestEntity> findByInitiator_idAndEvent_id(Long initiatorId, Long eventId);
}
