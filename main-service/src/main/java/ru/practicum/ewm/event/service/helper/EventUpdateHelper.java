package ru.practicum.ewm.event.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventUserRequestDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventUpdateHelper {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EntityHelper entityHelper;

    public void updateUserEventFields(Event event, UpdateEventUserRequestDto dto) {
        updateCommonFields(event, dto.getAnnotation(), dto.getDescription(), dto.getEventDate(),
                dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration(),
                dto.getTitle(), dto.getCategory(), dto.getLocation());

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            }
        }
    }

    public void updateAdminEventFields(Event event, UpdateEventAdminRequestDto dto) {
        updateCommonFields(event, dto.getAnnotation(), dto.getDescription(), dto.getEventDate(),
                dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration(),
                dto.getTitle(), dto.getCategory(), dto.getLocation());

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

    public Event getUserOwnedEvent(Long userId, Long eventId) {
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

    public Location createLocation(NewEventRequestDto newEventDto) {
        Location newLocation = Location.builder()
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
        return locationRepository.save(newLocation);
    }

    public void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Поле 'eventDate' должно содержать дату, которая ещё не наступила");
        }
    }

    private void updateCommonFields(Event event,
                                    String annotation,
                                    String description,
                                    String eventDateStr,
                                    Boolean paid,
                                    Integer participantLimit,
                                    Boolean requestModeration,
                                    String title,
                                    Long categoryId,
                                    ru.practicum.ewm.location.dto.LocationDto locationDto) {

        if (annotation != null) {
            event.setAnnotation(annotation);
        }

        if (description != null) {
            event.setDescription(description);
        }

        if (eventDateStr != null) {
            LocalDateTime newDate = LocalDateTime.parse(eventDateStr, FORMATTER);
            event.setEventDate(newDate);
        }

        if (paid != null) event.setPaid(paid);

        if (participantLimit != null) {
            if (participantLimit < 0) {
                throw new ValidationException("Лимит не должен быть меньше нуля");
            }
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        if (title != null) {
            event.setTitle(title);
        }

        if (categoryId != null && !categoryId.equals(event.getCategory().getId())) {
            Category category = entityHelper.getExistingCategoryByIdOrThrow(categoryId);
            event.setCategory(category);
        }

        if (locationDto != null) {
            float newLat = locationDto.getLat();
            float newLon = locationDto.getLon();
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
}