package ru.practicum.client;

import ru.practicum.dto.EndpointHitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Qualifier("asyncClient")
public class StatsWebClient implements StatsClient {
    private final WebClient webClient;

    public StatsWebClient(String uri) {
        this.webClient = WebClient.create(uri);
    }

    @Override
    public void postStats(String app, String uri, String ip, String timestamp) {
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
            uri.append("&uri=").append(u);
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