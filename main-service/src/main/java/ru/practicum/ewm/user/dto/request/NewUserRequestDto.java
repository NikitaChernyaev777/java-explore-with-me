package ru.practicum.ewm.user.dto.request;

import jakarta.validation.constraints.Email;
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
public class NewUserRequestDto {

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 250, message = "Имя пользователя должно содержать не менее 2 и не более 250 символов")
    private String name;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email указан в некорректном формате")
    @Size(min = 6, max = 254, message = "Email должен содержать не менее 6 и не более 254 символов")
    private String email;
}