package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static ru.practicum.ewm.constant.DateTimeFormatters.DATE_TIME_FORMAT;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewEndpointHitRequestDto {

    @NotBlank(message = "Идентификатор сервиса не должен быть пустым")
    @Size(max = 255, message = "Название идентификатора сервиса не должно превышать 255 символов")
    private String app;

    @NotBlank(message = "URI не должен быть пустым")
    @Size(max = 255, message = "URI не должен превышать 255 символов")
    @Pattern(regexp = "^/.*", message = "В начале URI должен быть указан символ слэша(/)")
    private String uri;

    @NotBlank(message = "IP-адрес пользователя не должен быть пустым")
    @Size(max = 45, message = "IP-адрес пользователя не должен превышать 45 символов")
    private String ip;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @NotNull(message = "Дата и время, когда был совершен запрос к эндпоинту, не должны быть пустыми")
    private String timestamp;
}