package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.request.NewCommentRequestDto;
import ru.practicum.ewm.comment.dto.request.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;

public interface PrivateCommentService {

    CommentResponseDto createComment(NewCommentRequestDto newCommentDto, Long userId, Long eventId);

    CommentResponseDto updateComment(UpdateCommentRequestDto updateCommentDto,
                                     Long userId,
                                     Long eventId,
                                     Long commentId);

    void deleteComment(Long userId, Long eventId, Long commentId);
}