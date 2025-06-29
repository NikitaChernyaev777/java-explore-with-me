package ru.practicum.ewm.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;
import ru.practicum.ewm.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.practicum.ewm.dto.response.ViewStatsResponseDto(eh.app, eh.uri, COUNT(eh.ip))
            FROM EndpointHit eh
            WHERE eh.timestamp BETWEEN :start AND :end
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(eh.ip) DESC
            """)
    List<ViewStatsResponseDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT new ru.practicum.ewm.dto.response.ViewStatsResponseDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
            FROM EndpointHit eh
            WHERE eh.timestamp BETWEEN :start AND :end
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(DISTINCT eh.ip) DESC
            """)
    List<ViewStatsResponseDto> getUniqueStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT new ru.practicum.ewm.dto.response.ViewStatsResponseDto(eh.app, eh.uri, COUNT(eh.ip))
            FROM EndpointHit eh
            WHERE eh.timestamp BETWEEN :start AND :end AND eh.uri IN :uris
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(eh.ip) DESC
            """)
    List<ViewStatsResponseDto> getStatsByUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("""
            SELECT new ru.practicum.ewm.dto.response.ViewStatsResponseDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
            FROM EndpointHit eh
            WHERE eh.timestamp BETWEEN :start AND :end AND eh.uri IN :uris
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(DISTINCT eh.ip) DESC
            """)
    List<ViewStatsResponseDto> getUniqueStatsByUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}