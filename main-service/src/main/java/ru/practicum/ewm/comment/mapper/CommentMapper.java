package ru.practicum.ewm.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.comment.dto.request.NewCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.user.mapper.UserMapper;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toNewComment(NewCommentRequestDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(final Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortResponseDto(comment.getAuthor()))
                .createdOn(comment.getCreatedOn().format(FORMATTER))
                .updatedOn(comment.getUpdatedOn().format(FORMATTER))
                .build();
    }
}