package ru.practicum.client;

public interface StatsClient {
    void postStats(String app, String uri, String ip, String timestamp);
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}