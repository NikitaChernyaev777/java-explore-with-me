package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventUserRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.helper.EventUpdateHelper;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.request.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.request.dto.response.EventRequestStatusUpdateResultResponseDto;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityHelper entityHelper;
    private final EventUpdateHelper eventUpdateHelper;

    @Override
    @Transactional
    public EventFullResponseDto createEvent(NewEventRequestDto newEventDto, Long userId) {
        log.info("Создание события пользователем с id={} и заголовком '{}'", userId, newEventDto.getTitle());

        Event newEvent = EventMapper.toNewEvent(newEventDto);
        eventUpdateHelper.validateEventDate(newEvent.getEventDate());

        newEvent.setInitiator(entityHelper.getExistingUserByIdOrThrow(userId));
        newEvent.setCategory(entityHelper.getExistingCategoryByIdOrThrow(newEventDto.getCategory()));
        newEvent.setLocation(eventUpdateHelper.createLocation(newEventDto));
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
        Event event = eventUpdateHelper.getUserOwnedEvent(userId, eventId);
        log.info("Событие с id={} успешно найдено и принадлежит пользователю с id={}", eventId, userId);
        return EventMapper.toEventFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventFullResponseDto updateUserEvent(UpdateEventUserRequestDto updateEventUserDto,
                                                Long userId,
                                                Long eventId) {
        log.info("Обновление события с id={} пользователем с id={}", eventId, userId);

        Event event = eventUpdateHelper.getUserOwnedEvent(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Данное событие недоступно для обновления");
        }

        if (updateEventUserDto.getEventDate() != null) {
            eventUpdateHelper.validateEventDate(LocalDateTime.parse(updateEventUserDto.getEventDate(), FORMATTER));
        }

        eventUpdateHelper.updateUserEventFields(event, updateEventUserDto);
        Event updatedUserEvent = eventRepository.save(event);

        log.info("Событие с id={} успешно обновлено пользователем", updatedUserEvent.getId());
        return EventMapper.toEventFullResponseDto(updatedUserEvent);
    }

    @Override
    public List<ParticipationRequestResponseDto> getParticipationRequests(Long userId, Long eventId) {
        log.info("Получение заявок на участие для события с id={} пользователя с id={}", eventId, userId);

        eventUpdateHelper.getUserOwnedEvent(userId, eventId);

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

        Event event = eventUpdateHelper.getUserOwnedEvent(userId, eventId);

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
}