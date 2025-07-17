package ru.practicum.ewm.comment.dto.request;

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
public class NewCommentRequestDto {

    @NotBlank(message = "Содержание комментария не должно быть пустым")
    @Size(min = 30, max = 2000, message = "Комментарий должен содержать не менее 30 и не более 2000 символов")
    private String text;
}