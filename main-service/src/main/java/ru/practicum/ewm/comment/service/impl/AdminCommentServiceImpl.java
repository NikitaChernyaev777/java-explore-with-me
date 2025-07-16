package ru.practicum.ewm.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.request.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.AdminCommentService;
import ru.practicum.ewm.helper.EntityHelper;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final EntityHelper entityHelper;

    @Override
    @Transactional
    public CommentResponseDto updateCommentByAdmin(UpdateCommentRequestDto updateCommentDto,
                                                   Long eventId,
                                                   Long commentId) {
        log.info("Обновление администратором комментария с id={} к событию с id={}", commentId, eventId);

        entityHelper.getExistingEventByIdOrThrow(eventId);
        Comment comment = entityHelper.getExistingCommentByIdOrThrow(commentId);
        entityHelper.validateCommentBelongsToEvent(eventId, comment);

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        log.info("Комментарий с id={} к событию с id={} успешно обновлен администратором", commentId, eventId);
        return CommentMapper.toCommentResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long eventId, Long commentId) {
        log.info("Удаление комментария c id={} к событию с id={} администратором", commentId, eventId);

        entityHelper.getExistingEventByIdOrThrow(eventId);
        Comment comment = entityHelper.getExistingCommentByIdOrThrow(commentId);
        entityHelper.validateCommentBelongsToEvent(eventId, comment);

        commentRepository.delete(comment);
        log.info("Комментарий с id={} к событию с id={} успешно удален администратором", commentId, eventId);
    }
}