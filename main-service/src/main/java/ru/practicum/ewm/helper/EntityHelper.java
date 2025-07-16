package ru.practicum.ewm.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EntityHelper {

    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Category getExistingCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%d не найдена", categoryId)));
    }

    public Comment getExistingCommentByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id=%d не найден", commentId)));
    }

    public Event getExistingEventByIdOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d не найдено", eventId)));
    }

    public User getExistingUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
    }

    public void validateCommentBelongsToEvent(Long eventId, Comment comment) {
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException(
                    String.format("Комментарий с id=%d не относится к событию с id=%d", comment.getId(), eventId));
        }
    }

    public void validateCommentOwnership(Long userId, Long eventId, Comment comment) {
        validateCommentBelongsToEvent(eventId, comment);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ConflictException(
                    String.format("Комментарий с id=%d не принадлежит пользователю с id=%d", comment.getId(), userId)
            );
        }
    }

    public Pageable toPageRequest(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}