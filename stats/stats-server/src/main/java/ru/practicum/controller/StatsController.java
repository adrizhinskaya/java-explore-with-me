package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.logger.ColoredCRUDLogger;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit create(@RequestBody EndpointHitDto endpointDto) {
        ColoredCRUDLogger.logPost("STATS-SERVER /hit:", endpointDto.toString());
        return statsService.create(endpointDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getAll(@RequestParam(name = "start") String start,
                                  @RequestParam(name = "end") String end,
                                  @RequestParam(required = false, name = "uris") Set<String> uris,
                                  @RequestParam(required = false, name = "unique", defaultValue = "false")
                                  boolean unique) {
        uris = uris == null ? Collections.emptySet() : uris;
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        String url = String.format("STATS-SERVER /stats?start={%s}&end{%s}&uris{%s}&unique{%s}",
                start, end, uris.toString(), unique);
        ColoredCRUDLogger.logGet(url);
        return statsService.getAll(startTime, endTime, uris, unique);
    }
}