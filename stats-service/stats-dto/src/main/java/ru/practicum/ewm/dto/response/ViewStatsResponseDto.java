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
public class ViewStatsResponseDto {
    private final String app;
    private final String uri;
    private final Long hits;

    @JsonCreator
    public ViewStatsResponseDto(
            @JsonProperty("app") String app,
            @JsonProperty("uri") String uri,
            @JsonProperty("hits") Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}