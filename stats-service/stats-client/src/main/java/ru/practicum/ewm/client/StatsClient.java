package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.request.NewEndpointHitRequestDto;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-service.url}") String serverUri, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUri))
                .build());
    }

    public ResponseEntity<Void> addHit(NewEndpointHitRequestDto endpointHitDto) {
        return post("/hit", endpointHitDto, new ParameterizedTypeReference<>() {
        });
    }

    public ResponseEntity<List<ViewStatsResponseDto>> getStats(String start, String end,
                                                               List<String> uris, boolean unique) {
        Map<String, Object> params = Map.of(
                "start", encode(start),
                "end", encode(end),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                params,
                new ParameterizedTypeReference<>() {
                });
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}