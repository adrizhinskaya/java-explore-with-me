package ru.practicum.client;

import ru.practicum.dto.EndpointHitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postStat(EndpointHitDto endpointDto) {
        return post("/hit", endpointDto);
    }

    public ResponseEntity<Object> getAll(String start, String end, String uris, boolean unique) {
        checkDates(start, end);
        uris = uris == null ? "" : uris;
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    private void checkDates(String start, String end) {
        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);
        LocalDateTime now = LocalDateTime.now();

        boolean isStartBeforeEnd = startTime.isAfter(endTime);
        boolean isStartEqualEnd = startTime.isEqual(endTime);
        boolean isStartInFuture = startTime.isAfter(now);

        if (isStartBeforeEnd || isStartEqualEnd || isStartInFuture) {
            throw new RuntimeException("Wrong start or end parameter");
        }
    }
}