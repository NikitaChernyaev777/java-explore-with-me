package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event toNewEvent(NewEventRequestDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER))
                .paid(newEventDto.isPaid())
                .requestModeration(newEventDto.isRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullResponseDto toEventFullResponseDto(final Event event) {
        return EventFullResponseDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryResponseDto(event.getCategory()))
                .confirmedRequests(event.getParticipationRequests() != null
                        ? (long) event.getParticipationRequests().size() : 0)
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(UserMapper.toUserShortResponseDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortResponseDto toEventShortResponseDto(final Event event) {
        return EventShortResponseDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getParticipationRequests() != null
                        ? (long) event.getParticipationRequests().size() : 0)
                .category((CategoryMapper.toCategoryResponseDto(event.getCategory())))
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(UserMapper.toUserShortResponseDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}