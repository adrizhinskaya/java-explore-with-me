package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {
    @Query("SELECT new ru.practicum.dto.ViewStats(n.app, n.uri, COUNT(n.ip)) " +
            "FROM EndpointHit n " +
            "WHERE n.timestamp BETWEEN :start AND :end " +
            "GROUP BY n.app, n.uri " +
            "ORDER BY COUNT(n.ip) DESC")
    List<ViewStats> getStats(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats(n.app, n.uri, COUNT(DISTINCT n.ip)) " +
            "FROM EndpointHit n " +
            "WHERE n.timestamp BETWEEN :start AND :end " +
            "GROUP BY n.app, n.uri " +
            "ORDER BY COUNT(DISTINCT n.ip) DESC")
    List<ViewStats> getStatsWithUniqueIp(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats(n.app, n.uri, COUNT(n.ip)) " +
            "FROM EndpointHit n " +
            "WHERE n.timestamp BETWEEN :start AND :end " +
            "AND n.uri IN :uris " +
            "GROUP BY n.app, n.uri " +
            "ORDER BY COUNT(n.ip) DESC")
    List<ViewStats> getStatsByUris(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") Set<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStats(n.app, n.uri, COUNT(DISTINCT n.ip)) " +
            "FROM EndpointHit n " +
            "WHERE n.timestamp BETWEEN :start AND :end " +
            "AND n.uri IN :uris " +
            "GROUP BY n.app, n.uri " +
            "ORDER BY COUNT(DISTINCT n.ip) DESC")
    List<ViewStats> getStatsByUrisWithUniqueIp(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") Set<String> uris);
}