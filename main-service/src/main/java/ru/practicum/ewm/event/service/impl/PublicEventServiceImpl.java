package ru.practicum.ewm.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.PublicEventService;
import ru.practicum.ewm.event.service.helper.EventStatsService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.helper.EntityHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final EventStatsService eventStatsService;
    private final EntityHelper entityHelper;

    @Override
    public List<EventShortResponseDto> searchPublicEvents(String text,
                                                          List<Long> categories,
                                                          Boolean paid,
                                                          String rangeStart,
                                                          String rangeEnd,
                                                          Boolean onlyAvailable,
                                                          String sort,
                                                          int from,
                                                          int size) {
        log.info("Поиск публичных событий: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(QEvent.event.state.eq(EventState.PUBLISHED));

        if (categories != null && !categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }

        if (paid != null) {
            predicate.and((QEvent.event.paid.eq(paid)));
        }

        if (text != null && !text.isBlank()) {
            predicate.and(QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text)));
        }

        if (rangeStart != null && !rangeStart.isBlank() &&
                rangeEnd != null && !rangeEnd.isBlank()) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, FORMATTER);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);

            if (start.isAfter(end)) {
                throw new ValidationException("Начало диапазона должно быть раньше конца диапазона");
            }

            predicate.and(QEvent.event.eventDate.between(start, end));
        } else {
            predicate.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            predicate.and(QEvent.event.participantLimit.eq(0)
                    .or(QEvent.event.participationRequests.size().lt(QEvent.event.participantLimit)));
        }

        Sort criteria = switch (Objects.requireNonNullElse(sort, "")) {
            case "EVENT_DATE" -> Sort.by(Sort.Direction.DESC, "eventDate");
            case "VIEWS" -> Sort.by(Sort.Direction.DESC, "views");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };

        Pageable pageRequest = PageRequest.of(from / size, size, criteria);

        List<Event> events = eventRepository.findAll(predicate, pageRequest).stream().toList();
        eventStatsService.updateEventsViews(events);

        log.info("Найдено {} событий по заданным параметрам", events.size());
        return events.stream()
                .map(EventMapper::toEventShortResponseDto)
                .toList();
    }

    @Override
    public EventFullResponseDto getEventById(Long eventId) {
        log.info("Получение события с id={}", eventId);

        Event event = entityHelper.getExistingEventByIdOrThrow(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id=%d не опубликовано".formatted(eventId));
        }

        eventStatsService.updateEventViews(event);

        log.info("Событие с id={} успешно найдено и просмотры обновлены", eventId);
        return EventMapper.toEventFullResponseDto(event);
    }
}