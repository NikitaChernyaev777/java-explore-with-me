package ru.practicum.ewm.compilation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationRequestDto {

    private List<Long> events;

    private Boolean pinned;

    @NotBlank(message = "Заголовок подборки не должен быть пустым")
    @Size(min = 1, max = 50, message = "Заголовок подборки должен содержать не менее 1 и не более 50 символов")
    private String title;
}