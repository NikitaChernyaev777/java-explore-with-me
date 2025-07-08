package ru.practicum.ewm.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.location.dto.LocationDto;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewEventRequestDto {

    @NotBlank(message = "Краткое описание события не должно быть пустым")
    @Size(min = 20, max = 2000, message = "Аннотация должна содержать не менее 20 и не более 2000 символов")
    private String annotation;

    @NotNull(message = "Поле 'category' не должно быть пустым")
    private Long category;

    @NotBlank(message = "Описание не должен быть пустым")
    @Size(min = 20, max = 7000, message = "Описание должно содержать не менее 20 и не более 7000 символов")
    private String description;

    @NotBlank(message = "Дата и время, на которые намечено событие, не должен быть пустыми")
    private String eventDate;

    @NotNull(message = "Поле 'location' не должно быть пустым")
    private LocationDto location;

    private boolean paid;

    @PositiveOrZero(message = "Лимит пользователей должен быть не меньше 0")
    private Integer participantLimit;

    @Builder.Default
    private boolean requestModeration = true;

    @NotBlank(message = "Заголовок события не должен быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен содержать не менее 3 и не более 120 символов")
    private String title;
}