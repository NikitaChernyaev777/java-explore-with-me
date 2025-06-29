package ru.practicum.ewm.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.request.NewEndpointHitRequestDto;
import ru.practicum.ewm.dto.response.EndpointHitResponseDto;
import ru.practicum.ewm.server.model.EndpointHit;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitMapper {

    public static EndpointHit toNewEndpointHit(NewEndpointHitRequestDto newEndpointHitDto) {
        return EndpointHit.builder()
                .app(newEndpointHitDto.getApp())
                .uri(newEndpointHitDto.getUri())
                .ip(newEndpointHitDto.getIp())
                .timestamp(LocalDateTime.parse(newEndpointHitDto.getTimestamp(), FORMATTER))
                .build();
    }

    public static EndpointHitResponseDto toEndpointHitResponseDto(final EndpointHit endpointHit) {
        return EndpointHitResponseDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp().format(FORMATTER))
                .build();
    }
}