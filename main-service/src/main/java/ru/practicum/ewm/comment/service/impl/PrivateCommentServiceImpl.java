package ru.practicum.ewm.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.request.NewCommentRequestDto;
import ru.practicum.ewm.comment.dto.request.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.PrivateCommentService;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.helper.EntityHelper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final EntityHelper entityHelper;

    @Override
    @Transactional
    public CommentResponseDto createComment(NewCommentRequestDto newCommentDto,
                                            Long userId,
                                            Long eventId) {
        log.info("Создание комментария пользователем с id={} к событию с id={}", userId, eventId);

        Event event = entityHelper.getExistingEventByIdOrThrow(eventId);
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("Событие с id=%d не опубликовано", eventId));
        }
        User user = entityHelper.getExistingUserByIdOrThrow(userId);

        Comment newComment = CommentMapper.toNewComment(newCommentDto);
        newComment.setEvent(event);
        newComment.setAuthor(user);
        newComment.setCreatedOn(LocalDateTime.now());

        Comment savedComment = commentRepository.save(newComment);

        log.info("Комментарий с id={} к событию с id={} успешно создан", savedComment.getId(), eventId);
        return CommentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(UpdateCommentRequestDto updateCommentDto,
                                            Long userId,
                                            Long eventId,
                                            Long commentId) {
        log.info("Обновление комментария с id={} пользователем с id={} к событию с id={}", commentId, userId, eventId);

        entityHelper.getExistingEventByIdOrThrow(eventId);
        Comment comment = entityHelper.getExistingCommentByIdOrThrow(commentId);
        entityHelper.getExistingUserByIdOrThrow(userId);
        entityHelper.validateCommentOwnership(userId, eventId, comment);

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        log.info("Комментарий с id={} к событию с id={} успешно обновлен пользователем с id={}",
                commentId, eventId, userId);
        return CommentMapper.toCommentResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        log.info("Удаление комментария c id={} к событию с id={} пользователем с id={}", commentId, eventId, userId);

        entityHelper.getExistingEventByIdOrThrow(eventId);
        Comment comment = entityHelper.getExistingCommentByIdOrThrow(commentId);
        entityHelper.getExistingUserByIdOrThrow(userId);
        entityHelper.validateCommentOwnership(userId, eventId, comment);

        commentRepository.delete(comment);
        log.info("Комментарий с id={} к событию с id={} успешно удален пользователем с id={}",
                commentId, eventId, userId);
    }
}