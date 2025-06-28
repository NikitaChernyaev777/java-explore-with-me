package ru.practicum.ewm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ViewStatsResponseDto {
    private final String app;
    private final String uri;
    private final Long hits;
}