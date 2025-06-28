package ru.practicum.ewm.server.service;

import ru.practicum.ewm.dto.request.NewEndpointHitRequestDto;
import ru.practicum.ewm.dto.response.EndpointHitResponseDto;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    EndpointHitResponseDto createEndpointHit(NewEndpointHitRequestDto newEndpointHitDto);

    List<ViewStatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}