package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient;
import ru.practicum.logger.ColoredCRUDLogger;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class MainController {
    @Autowired
    @Qualifier("asyncClient")
    private StatsClient statsClient;

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
        statsClient.postStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        return null;
        //return statsWebClient.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,size);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Object> getEventByID(@PathVariable Integer id,
                                               HttpServletRequest request) {
        String url = String.format("MAIN-SERVICE /events/{%s}", id);
        ColoredCRUDLogger.logGet(url);
        statsClient.postStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        return null;
        //return statsWebClient.getEventByID(id);
    }
}
