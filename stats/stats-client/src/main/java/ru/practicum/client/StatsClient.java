package ru.practicum.client;

import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void postStats(String app, String uri, String ip, LocalDateTime timestamp);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}