package ru.practicum.event;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShort;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewsService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;

    public List<EventShortDto> toEventShortDtos(List<EventEntity> events, Boolean unique) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> eventIds = events.stream().map(EventEntity::getId).collect(Collectors.toList());
        Map<Long, Long> hitsMap = getEventsViewsMap(null, null, eventIds, unique);
        Map<Long, Long> eventsConfirmedRequests = getConfirmedRequests(eventIds);
        return events.stream()
                .map(event -> {
                    Long views = hitsMap.get(event.getId());
                    Long confReq = eventsConfirmedRequests.get(event.getId());
                    return eventMapper.toEventShortDto(event,
                            confReq != null ? confReq : 0L,
                            views != null ? views : 0L);
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventIds) {
        List<EventShort> events = requestRepository.countAllByEventIdsAndStatusIs(eventIds, RequestStatus.CONFIRMED);
        return events.stream()
                .collect(Collectors.toMap(EventShort::getEventId, EventShort::getConfirmedRequests));
    }

    public List<EventFullDto> toEventFullDtos(@Nullable LocalDateTime start, @Nullable LocalDateTime end,
                                              List<EventEntity> events, Boolean unique) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> eventIds = events.stream().map(EventEntity::getId).collect(Collectors.toList());
        Map<Long, Long> hitsMap = getEventsViewsMap(start, end, eventIds, unique);
        Map<Long, Long> eventsConfirmedRequests = getConfirmedRequests(eventIds);
        return events.stream()
                .map(event -> {
                    Long views = hitsMap.get(event.getId());
                    Long confReq = eventsConfirmedRequests.get(event.getId());
                    return eventMapper.toEventFullDto(event,
                            confReq != null ? confReq : 0L,
                            views != null ? views : 0L);
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getEventsViewsMap(@Nullable LocalDateTime start, @Nullable LocalDateTime end,
                                              List<Long> eventIds, Boolean unique) {
        List<String> itemUris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        String startFormatter = start != null ? start.format(formatter) : "1970-01-01 00:00:00";
        String endFormatter = end != null ? end.format(formatter) : "9999-12-31 23:59:59";
        List<ViewStats> stats = statsClient.getStats(startFormatter, endFormatter, itemUris, unique);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractIdFromUri(stat.getUri()),
                        ViewStats::getHits
                ));
    }

    private Long extractIdFromUri(String uri) {
        String idString = uri.replace("/events/", "");
        return Long.valueOf(idString);
    }
}