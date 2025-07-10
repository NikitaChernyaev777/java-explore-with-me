package ru.practicum.ewm.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryRequestDto {

    @NotBlank(message = "Наименование категории не должен быть пустым")
    @Size(min = 1, max = 50, message = "Наименование категории должно содержать не менее 1 и не более 50 символов")
    private String name;
}