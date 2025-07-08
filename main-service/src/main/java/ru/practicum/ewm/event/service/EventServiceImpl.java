package ru.practicum.ewm.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.response.ViewStatsResponseDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventUserRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.request.dto.response.EventRequestStatusUpdateResultResponseDto;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements PrivateEventService, AdminEventService, PublicEventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityHelper entityHelper;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullResponseDto createEvent(NewEventRequestDto newEventDto, Long userId) {
        log.info("Создание события пользователем с id={} и заголовком '{}'", userId, newEventDto.getTitle());

        Event newEvent = EventMapper.toNewEvent(newEventDto);
        validateEventDate(newEvent.getEventDate());

        newEvent.setInitiator(entityHelper.getExistingUserByIdOrThrow(userId));
        newEvent.setCategory(entityHelper.getExistingCategoryByIdOrThrow(newEventDto.getCategory()));
        newEvent.setLocation(createLocation(newEventDto));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(EventState.PENDING);

        Event savedEvent = eventRepository.save(newEvent);

        log.info("Событие с id={} успешно создано", savedEvent.getId());
        return EventMapper.toEventFullResponseDto(savedEvent);
    }

    @Override
    public List<EventShortResponseDto> getUserEvents(Long userId, int from, int size) {
        log.info("Получение списка событий пользователя с id={}, from={}, size={}", userId, from, size);

        Pageable pageRequest = entityHelper.toPageRequest(from, size);

        List<EventShortResponseDto> events = eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(EventMapper::toEventShortResponseDto)
                .collect(Collectors.toList());

        log.info("Найдено {} событий для пользователя с id={}", events.size(), userId);
        return events;
    }

    @Override
    public EventFullResponseDto getUserEventById(Long userId, Long eventId) {
        log.info("Получение события с id={} для пользователя с id={}", eventId, userId);
        Event event = getUserOwnedEvent(userId, eventId);
        log.info("Событие с id={} успешно найдено и принадлежит пользователю с id={}", eventId, userId);
        return EventMapper.toEventFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventFullResponseDto updateUserEvent(UpdateEventUserRequestDto updateEventUserDto,
                                                Long userId,
                                                Long eventId) {
        log.info("Обновление события с id={} пользователем с id={}", eventId, userId);

        Event event = getUserOwnedEvent(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Данное событие недоступно для обновления");
        }

        if (updateEventUserDto.getEventDate() != null) {
            validateEventDate(LocalDateTime.parse(updateEventUserDto.getEventDate(), FORMATTER));
        }

        updateUserEventFields(event, updateEventUserDto);
        Event updatedUserEvent = eventRepository.save(event);

        log.info("Событие с id={} успешно обновлено пользователем", updatedUserEvent.getId());
        return EventMapper.toEventFullResponseDto(updatedUserEvent);
    }

    @Override
    public List<ParticipationRequestResponseDto> getParticipationRequests(Long userId, Long eventId) {
        log.info("Получение заявок на участие для события с id={} пользователя с id={}", eventId, userId);

        getUserOwnedEvent(userId, eventId);

        List<ParticipationRequest> requests = participationRequestRepository.findAllByEventId(eventId);

        log.info("Найдено {} заявок на участие для события с id={}", requests.size(), eventId);
        return requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultResponseDto updateParticipationRequestStatuses(
            EventRequestStatusUpdateRequestDto updateRequest, Long userId, Long eventId) {
        log.info("Обновление статусов заявок на участие для события с id={} пользователем с id={}", eventId, userId);

        Event event = getUserOwnedEvent(userId, eventId);

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            log.info("Для события с id={} модерация заявок отключена или нет лимита", eventId);
            return EventRequestStatusUpdateResultResponseDto.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(List.of())
                    .build();
        }

        long confirmedCount = event.getParticipationRequests() == null ? 0 :
                event.getParticipationRequests().stream()
                        .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                        .count();

        if (confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников для события с id=" + eventId + " уже достигнут");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(updateRequest.getRequestIds());

        EventRequestStatusUpdateResultResponseDto result = EventRequestStatusUpdateResultResponseDto.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                continue;
            }

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Можно изменять только заявки в статусе 'PENDING'");
            }

            if (updateRequest.getStatus() == RequestStatus.CONFIRMED &&
                    confirmedCount < event.getParticipantLimit()) {

                request.setStatus(RequestStatus.CONFIRMED);
                confirmedCount++;
                result.getConfirmedRequests()
                        .add(ParticipationRequestMapper.toParticipationRequestResponseDto(
                                participationRequestRepository.save(request)));

            } else {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests()
                        .add(ParticipationRequestMapper.toParticipationRequestResponseDto(
                                participationRequestRepository.save(request)));
            }
        }

        log.info("Подтверждено заявок: {}, отклонено: {}",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }

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
            validateEventDate(LocalDateTime.parse(updateEventAdminDto.getEventDate(), FORMATTER));
        }

        updateAdminEventFields(event, updateEventAdminDto);
        Event updatedAdminEvent = eventRepository.save(event);

        log.info("Событие с id={} успешно обновлено администратором", updatedAdminEvent.getId());
        return EventMapper.toEventFullResponseDto(updatedAdminEvent);
    }

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
        updateEventsViews(events);

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

        updateEventViews(event);

        log.info("Событие с id={} успешно найдено и просмотры обновлены", eventId);
        return EventMapper.toEventFullResponseDto(event);
    }

    private Event getUserOwnedEvent(Long userId, Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> {
                    if (!event.getInitiator().getId().equals(userId)) {
                        throw new NotFoundException(
                                String.format("Событие с id=%d не принадлежит пользователю с id=%d", eventId, userId));
                    }
                    return event;
                })
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
    }

    private Location createLocation(NewEventRequestDto newEventDto) {
        Location newLocation = Location.builder()
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
        return locationRepository.save(newLocation);
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Поле 'eventDate' должно содержать дату, которая еще не наступила");
        }
    }

    private void updateEventViews(Event event) {
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

    private void updateEventsViews(List<Event> events) {
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

    private void updateUserEventFields(Event event, UpdateEventUserRequestDto dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
            event.setEventDate(newDate);
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 0) {
                throw new ValidationException("Лимит не должен быть меньше нуля");
            }
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            }
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getCategory() != null &&
                !dto.getCategory().equals(event.getCategory().getId())) {

            Category category = entityHelper.getExistingCategoryByIdOrThrow(dto.getCategory());
            event.setCategory(category);
        }

        if (dto.getLocation() != null) {
            float newLat = dto.getLocation().getLat();
            float newLon = dto.getLocation().getLon();
            float oldLat = event.getLocation().getLat();
            float oldLon = event.getLocation().getLon();

            if (Float.compare(newLat, oldLat) != 0 || Float.compare(newLon, oldLon) != 0) {
                Location newLocation = Location.builder()
                        .lat(newLat)
                        .lon(newLon)
                        .build();
                locationRepository.save(newLocation);
                event.setLocation(newLocation);
            }
        }
    }

    private void updateAdminEventFields(Event event, UpdateEventAdminRequestDto dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
            event.setEventDate(newDate);
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 0) {
                throw new ValidationException("Лимит не должен быть меньше нуля");
            }
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getCategory() != null &&
                !dto.getCategory().equals(event.getCategory().getId())) {
            Category category = entityHelper.getExistingCategoryByIdOrThrow(dto.getCategory());
            event.setCategory(category);
        }

        if (dto.getLocation() != null) {
            float newLat = dto.getLocation().getLat();
            float newLon = dto.getLocation().getLon();
            float oldLat = event.getLocation().getLat();
            float oldLon = event.getLocation().getLon();

            if (Float.compare(newLat, oldLat) != 0 || Float.compare(newLon, oldLon) != 0) {
                Location newLocation = Location.builder()
                        .lat(newLat)
                        .lon(newLon)
                        .build();
                locationRepository.save(newLocation);
                event.setLocation(newLocation);
            }
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState().equals(EventState.CANCELED)) {
                        throw new ConflictException("Нельзя опубликовать отменённое событие");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
            }
        }
    }
}