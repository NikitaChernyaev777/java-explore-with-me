package ru.practicum.ewm.event.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.enums.UserStateAction;
import ru.practicum.ewm.location.dto.LocationDto;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequestDto {

    @Size(min = 20, max = 2000, message = "Аннотация должна содержать не менее 20 и не более 2000 символов")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Описание должно содержать не менее 20 и не более 7000 символов")
    private String description;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Лимит пользователей должен быть не меньше 0")
    private Integer participantLimit;

    private Boolean requestModeration;

    private UserStateAction stateAction;

    @Size(min = 3, max = 120, message = "Заголовок должен содержать не менее 3 и не более 120 символов")
    private String title;
}