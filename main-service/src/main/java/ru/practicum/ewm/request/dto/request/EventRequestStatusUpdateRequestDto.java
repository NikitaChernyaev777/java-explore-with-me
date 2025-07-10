package ru.practicum.ewm.request.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequestDto {
    private List<Long> requestIds;
    private RequestStatus status;
}