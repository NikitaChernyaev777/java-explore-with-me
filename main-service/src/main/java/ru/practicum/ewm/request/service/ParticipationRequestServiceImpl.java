package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final EntityHelper entityHelper;

    @Override
    @Transactional
    public ParticipationRequestResponseDto createParticipationRequest(Long userId, Long eventId) {
        log.info("создание запроса на участие пользователем с id={} для события с id={}", userId, eventId);

        Event event = entityHelper.getExistingEventByIdOrThrow(eventId);
        validateParticipationRequest(event, userId);
        User user = entityHelper.getExistingUserByIdOrThrow(userId);

        ParticipationRequest newRequest = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status((event.getParticipantLimit() == 0 || !event.isRequestModeration())
                        ? RequestStatus.CONFIRMED
                        : RequestStatus.PENDING)
                .created(LocalDateTime.now())
                .build();

        ParticipationRequest savedRequest = participationRequestRepository.save(newRequest);

        log.info("Заявка на участие успешно создана: id={}, статус={}", savedRequest.getId(), savedRequest.getStatus());
        return ParticipationRequestMapper.toParticipationRequestResponseDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestResponseDto> getUserParticipationRequests(Long userId) {
        log.info("Получение информации о заявках пользователя с id={} на участие в чужих событиях", userId);

        entityHelper.getExistingUserByIdOrThrow(userId);

        List<ParticipationRequestResponseDto> requests = participationRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestResponseDto)
                .toList();

        log.info("Найдено {} заявок на участие для пользователя с id={}", requests.size(), userId);
        return requests;
    }

    @Override
    @Transactional
    public ParticipationRequestResponseDto cancelParticipationRequest(Long userId, Long requestId) {
        log.info("Отмена запроса на участие (id={}) пользователем с id={}", requestId, userId);

        ParticipationRequest request = getExistingParticipationRequestByIdOrThrow(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException(String.format(
                    "Запрос с id=%d не найден у пользователя с id=%d", requestId, userId));
        }

        request.setStatus(RequestStatus.CANCELED);

        ParticipationRequest canceledRequest = participationRequestRepository.save(request);

        log.info("Запрос с id={} успешно отменён", canceledRequest.getId());
        return ParticipationRequestMapper.toParticipationRequestResponseDto(canceledRequest);
    }

    private ParticipationRequest getExistingParticipationRequestByIdOrThrow(Long requestId) {
        return participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос на участие с id=%d не найден", requestId)));
    }

    private void validateParticipationRequest(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Нельзя отправить заявку на своё собственное событие");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя подать заявку на неопубликованное событие");
        }

        if (participationRequestRepository.findByRequesterIdAndEventId(userId, event.getId()) != null) {
            throw new ConflictException("Заявка уже существует");
        }

        if (event.getParticipantLimit() != 0) {
            int confirmedCount = participationRequestRepository
                    .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Лимит участников достигнут");
            }
        }
    }
}