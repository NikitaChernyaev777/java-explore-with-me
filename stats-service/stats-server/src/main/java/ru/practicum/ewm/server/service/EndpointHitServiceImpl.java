package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewEndpointHitRequestDto;
import ru.practicum.ewm.dto.response.EndpointHitResponseDto;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;
import ru.practicum.ewm.server.exception.ValidationException;
import ru.practicum.ewm.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public EndpointHitResponseDto createEndpointHit(NewEndpointHitRequestDto newEndpointHitDto) {
        log.info("Сохранение нового запроса: {}", newEndpointHitDto);
        EndpointHit newEndpointHit = EndpointHitMapper.toNewEndpointHit(newEndpointHitDto);
        EndpointHit savedEndpointHit = endpointHitRepository.save(newEndpointHit);
        return EndpointHitMapper.toEndpointHitResponseDto(savedEndpointHit);
    }

    @Override
    public List<ViewStatsResponseDto> getStats(LocalDateTime start,
                                               LocalDateTime end,
                                               List<String> uris,
                                               boolean unique) {
        log.info("Получение статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        validateTimeRange(start, end);

        return selectQueryMethod(start, end, uris, unique);
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Параметры 'start' и 'end' не могут быть null");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Параметр 'start' не может быть позже 'end'");
        }
    }

    private List<ViewStatsResponseDto> selectQueryMethod(LocalDateTime start,
                                                         LocalDateTime end,
                                                         List<String> uris,
                                                         boolean unique) {
        boolean hasUris = uris != null && !uris.isEmpty();

        if (unique) {
            return hasUris
                    ? endpointHitRepository.getUniqueStatsByUris(start, end, uris)
                    : endpointHitRepository.getUniqueStats(start, end);
        } else {
            return hasUris
                    ? endpointHitRepository.getStatsByUris(start, end, uris)
                    : endpointHitRepository.getStats(start, end);
        }
    }
}