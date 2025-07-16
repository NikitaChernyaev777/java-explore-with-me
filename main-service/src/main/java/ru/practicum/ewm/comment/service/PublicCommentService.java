package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.response.CommentResponseDto;

import java.util.List;

public interface PublicCommentService {

    List<CommentResponseDto> getAllCommentsByEvent(Long eventId, int from, int size);

    CommentResponseDto getCommentByEventAndCommentId(Long eventId, Long commentId);
}