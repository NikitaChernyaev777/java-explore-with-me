package ru.practicum.ewm.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.dto.request.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.event.service.helper.EventUpdateHelper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.helper.EntityHelper;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final EventUpdateHelper eventUpdateHelper;
    private final EntityHelper entityHelper;

    @Override
    public List<EventFullResponseDto> searchEventsByAdminFilters(List<Long> users,
                                                                 List<EventState> states,
                                                                 List<Long> categories,
                                                                 String rangeStart,
                                                                 String rangeEnd,
                                                                 int from,
                                                                 int size) {
        log.info("Поиск событий администратором с фильтрами: users={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);

        BooleanBuilder predicate = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            predicate.and(QEvent.event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicate.and(QEvent.event.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null && !rangeStart.isBlank()) {
            predicate.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, FORMATTER)));
        }

        if (rangeEnd != null && !rangeEnd.isBlank()) {
            predicate.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, FORMATTER)));
        }

        Pageable pageRequest = entityHelper.toPageRequest(from, size);

        List<EventFullResponseDto> result = eventRepository.findAll(predicate, pageRequest).stream()
                .map(EventMapper::toEventFullResponseDto)
                .toList();

        log.info("Найдено {} событий по фильтрам", result.size());
        return result;
    }

    @Override
    @Transactional
    public EventFullResponseDto updateAdminEvent(UpdateEventAdminRequestDto updateEventAdminDto, Long eventId) {
        log.info("Обновление события с id={} администратором", eventId);

        Event event = entityHelper.getExistingEventByIdOrThrow(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Данное событие недоступно для обновления");
        }

        if (updateEventAdminDto.getEventDate() != null) {
            eventUpdateHelper.validateEventDate(LocalDateTime.parse(updateEventAdminDto.getEventDate(), FORMATTER));
        }

        eventUpdateHelper.updateAdminEventFields(event, updateEventAdminDto);
        Event updatedAdminEvent = eventRepository.save(event);

        log.info("Событие с id={} успешно обновлено администратором", updatedAdminEvent.getId());
        return EventMapper.toEventFullResponseDto(updatedAdminEvent);
    }
}