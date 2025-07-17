package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.request.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;

public interface AdminCommentService {

    CommentResponseDto updateCommentByAdmin(UpdateCommentRequestDto updateCommentDto, Long eventId, Long commentId);

    void deleteCommentByAdmin(Long eventId, Long commentId);
}