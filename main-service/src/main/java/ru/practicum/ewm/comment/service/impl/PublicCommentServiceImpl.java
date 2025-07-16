package ru.practicum.ewm.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.PublicCommentService;
import ru.practicum.ewm.helper.EntityHelper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;
    private final EntityHelper entityHelper;

    @Override
    public List<CommentResponseDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        log.info("Поиск комментариев к событию с id={}", eventId);

        Pageable page = entityHelper.toPageRequest(from, size);

        List<CommentResponseDto> result = commentRepository.findAllByEventId(eventId, page).stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();

        log.info("Найдено {} комментариев к событию с id={}", result.size(), eventId);
        return result;
    }

    @Override
    public CommentResponseDto getCommentByEventAndCommentId(Long eventId, Long commentId) {
        log.info("Поиск комментария с id={} к событию с id={}", commentId, eventId);

        Comment comment = entityHelper.getExistingCommentByIdOrThrow(commentId);
        entityHelper.validateCommentBelongsToEvent(eventId, comment);

        log.info("Найден комментарий с id={} к событию с id={}", commentId, eventId);
        return CommentMapper.toCommentResponseDto(comment);
    }
}