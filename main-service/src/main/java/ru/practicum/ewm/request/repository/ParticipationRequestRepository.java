package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    ParticipationRequest findByRequesterIdAndEventId(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);
}