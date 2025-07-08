package ru.practicum.ewm.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.request.NewEndpointHitRequestDto;
import ru.practicum.ewm.dto.response.EndpointHitResponseDto;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-service.url}") String serverUri, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUri))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build());
    }

    public EndpointHitResponseDto saveEvent(NewEndpointHitRequestDto endpointHitDto) throws IOException {
        ResponseEntity<Object> response = post(endpointHitDto);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(response.getBody(), EndpointHitResponseDto.class);

    }

    public List<ViewStatsResponseDto> getStats(LocalDateTime start,
                                               LocalDateTime end,
                                               List<String> uris,
                                               boolean unique) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("uris", String.join(",", uris));
        params.put("unique", unique);

        ResponseEntity<Object> response = get(params);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(response.getBody(),
                new TypeReference<List<ViewStatsResponseDto>>() {
                });
    }
}