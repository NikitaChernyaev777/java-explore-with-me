package ru.practicum.ewm.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.user.dto.response.UserShortResponseDto;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentResponseDto {
    private final Long id;
    private final String text;
    private final UserShortResponseDto author;
    private final String createdOn;
    private final String updatedOn;
}