package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.logger.ColoredCRUDLogger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final StatsClient statsClient;

    @GetMapping("/events")
    public ResponseEntity<Object> getEvents(@RequestParam(name = "text") String text,
                                            @RequestParam(name = "categories") Set<Integer> categories,
                                            @RequestParam(name = "categories") boolean paid,
                                            @RequestParam(name = "rangeStart") String rangeStart,
                                            @RequestParam(name = "rangeEnd") String rangeEnd,
                                            @RequestParam(name = "onlyAvailable", defaultValue = "false")
                                            boolean onlyAvailable,
                                            @RequestParam(name = "sort") String sort,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size,
                                            HttpServletRequest request) {

        String url = String.format("MAIN-SERVICE /events?text={%s}&categories={%s}&categories={%s}&" +
                        "rangeStart={%s}&rangeEnd={%s}&onlyAvailable={%s}&sort={%s}&from={%s}&size={%s}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        ColoredCRUDLogger.logGet(url);
        statsClient.postStat(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        return null;
        //return statsClient.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,size);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Object> getEventByID(@PathVariable Integer id,
                                               HttpServletRequest request) {
        String url = String.format("MAIN-SERVICE /events/{%s}", id);
        ColoredCRUDLogger.logGet(url);
        statsClient.postStat(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        return null;
        //return statsClient.getEventByID(id);
    }
}
