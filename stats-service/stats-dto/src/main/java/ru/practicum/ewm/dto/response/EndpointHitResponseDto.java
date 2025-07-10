package ru.practicum.ewm.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class EndpointHitResponseDto {
    private final Long id;
    private final String app;
    private final String uri;
    private final String ip;
    private final String timestamp;

    @JsonCreator
    public EndpointHitResponseDto(
            @JsonProperty("id") Long id,
            @JsonProperty("app") String app,
            @JsonProperty("uri") String uri,
            @JsonProperty("ip") String ip,
            @JsonProperty("timestamp") String timestamp) {
        this.id = id;
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}