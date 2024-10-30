package ru.practicum.event;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Collection;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.RequestStatus;

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
        if(events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> hitsMap = getEventsViewsMap(null, null, events, unique);
        return events.stream()
                .map(event -> {
                    Long views = hitsMap.get(event.getId());
                    return eventMapper.toEventShortDto(event, getConfirmedRequests(event.getId()),
                            views != null ? views : 0L);
                })
                .collect(Collectors.toList());
    }

    private Long getConfirmedRequests(Long eventId) {
        return requestRepository.countAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);
    }

    public List<EventFullDto> toEventFullDtos(@Nullable LocalDateTime start, @Nullable LocalDateTime end, List<EventEntity> events, Boolean unique) {
        Map<Long, Long> hitsMap = getEventsViewsMap(start, end, events, unique);
        return events.stream()
                .map(event -> {
                    Long views = hitsMap.get(event.getId());
                    return eventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()),
                            views != null ? views : 0L);
                })
                .collect(Collectors.toList());
    }
    private Map<Long, Long> getEventsViewsMap(@Nullable LocalDateTime start, @Nullable LocalDateTime end, List<EventEntity> events, Boolean unique) {
        List<String> itemUris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        String startFormatter = start != null ? start.format(formatter) : "1970-01-01 00:00:00";
        String endFormatter = end != null ? end.format(formatter) : "9999-12-31 23:59:59";
        List<ViewStats> stats = statsClient.getStats(startFormatter, endFormatter, itemUris, unique);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractIdFromUri(stat.getUri()), // Извлекаем id из uri
                        ViewStats::getHits // Получаем количество просмотров
                ));
    }

    private Long extractIdFromUri(String uri) {
        // Отбрасываем часть строки "/events/" и парсим id
        String idString = uri.replace("/events/", "");
        return Long.valueOf(idString);
    }
}
