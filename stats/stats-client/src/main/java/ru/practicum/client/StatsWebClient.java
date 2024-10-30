package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@Qualifier("asyncClient")
public class StatsWebClient implements StatsClient {
    private final WebClient webClient;

    @Autowired
    public StatsWebClient(@Value("${stats-server.url}") String url) {
        this.webClient = WebClient.create(url);
    }

    @Override
    public void postStats(String app, String uri, String ip, LocalDateTime timestamp) {
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new EndpointHitDto(app, uri, ip, timestamp))
                .retrieve()
                .toBodilessEntity()
                .block();

    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder uri = new StringBuilder("/stats?start=" + start + "&end=" + end);
        if (unique) {
            uri.append("&unique=true");
        }

        for (String u : uris) {
            uri.append("&uris=").append(u);
        }

        return webClient.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {
                })
                .block();
    }
}