package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EndpointHitMapper;
import ru.practicum.dto.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHit create(EndpointHitDto endpointDto) {
        return statsRepository.save(EndpointHitMapper.mapToEndpointHit(endpointDto));
    }

    @Override
    public List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {
        if (unique) {
            if (uris.isEmpty()) {
                return statsRepository.getStatsWithUniqueIp(start, end);
            }
            return statsRepository.getStatsByUrisWithUniqueIp(start, end, uris);
        }
        if (uris.isEmpty()) {
            return statsRepository.getStats(start, end);
        }
        return statsRepository.getStatsByUris(start, end, uris);
    }
}
