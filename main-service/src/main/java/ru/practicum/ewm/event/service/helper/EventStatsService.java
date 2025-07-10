package ru.practicum.ewm.event.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventStatsService {

    private final StatsClient statsClient;

    public void updateEventViews(Event event) {
        LocalDateTime start = LocalDateTime.now().minusMinutes(1);

        try {
            List<ViewStatsResponseDto> stats = statsClient.getStats(
                    start,
                    LocalDateTime.now(),
                    List.of("/events/" + event.getId()),
                    true);

            long views = (stats != null && !stats.isEmpty()) ? stats.getFirst().getHits() : 0L;
            event.setViews(views);

        } catch (Exception exception) {
            log.warn("Ошибка при получении статистики просмотров события с id={}: {}",
                    event.getId(), exception.getMessage());
            event.setViews(0L);
        }
    }

    public void updateEventsViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        try {
            List<ViewStatsResponseDto> statsList = statsClient.getStats(
                    start,
                    LocalDateTime.now(),
                    uris,
                    true
            );

            if (statsList != null && !statsList.isEmpty()) {
                for (Event event : events) {
                    String uri = "/events/" + event.getId();
                    statsList.stream()
                            .filter(stat -> uri.equals(stat.getUri()))
                            .findFirst()
                            .ifPresentOrElse(
                                    stat -> event.setViews(stat.getHits()),
                                    () -> event.setViews(0L)
                            );
                }
            } else {
                events.forEach(event -> event.setViews(0L));
            }

        } catch (Exception exception) {
            log.warn("Ошибка при получении статистики просмотров для {} событий: {}",
                    events.size(), exception.getMessage());
            events.forEach(event -> event.setViews(0L));
        }
    }
}