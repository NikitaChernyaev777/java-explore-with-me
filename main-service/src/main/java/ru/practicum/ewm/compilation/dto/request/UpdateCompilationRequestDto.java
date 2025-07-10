package ru.practicum.ewm.compilation.dto.request;

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
public class UpdateCompilationRequestDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Заголовок подборки должен содержать не менее 1 и не более 50 символов")
    private String title;
}