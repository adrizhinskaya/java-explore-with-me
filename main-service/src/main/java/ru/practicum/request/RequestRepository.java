package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.dto.EventShort;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    List<RequestEntity> findAllByIdIn(Set<Long> ids);

    Optional<RequestEntity> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<RequestEntity> findAllByRequesterId(Long userId);

    @Query("SELECT r " +
            "FROM RequestEntity r " +
            "WHERE r.event.id = :eventId AND r.event.initiator.id = :initiatorId")
    List<RequestEntity> findByInitiatorIdAndEventId(@Param("initiatorId") Long initiatorId,
                                                    @Param("eventId") Long eventId);

    Long countAllByEventIdAndStatusIs(Long eventId, RequestStatus status);
    //Long countAllByEventIdsAndStatusIs(List<Long> eventIds, RequestStatus status);
    @Query("SELECT new ru.practicum.event.model.dto.EventShort(r.event.id, COUNT(r.event.id)) " +
            "FROM RequestEntity r " +
            "WHERE r.event.id IN :eventIds AND r.status = :status " +
            "GROUP BY r.event.id")
    List<EventShort> countAllByEventIdsAndStatusIs(@Param("eventIds") List<Long> itemIds,
                                                   @Param("status") RequestStatus status);
}
