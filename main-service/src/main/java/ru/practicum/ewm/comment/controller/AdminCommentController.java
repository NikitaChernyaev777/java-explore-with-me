package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.request.UpdateCommentRequestDto;
import ru.practicum.ewm.comment.dto.response.CommentResponseDto;
import ru.practicum.ewm.comment.service.AdminCommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateCommentByAdmin(@Valid @RequestBody UpdateCommentRequestDto updateCommentDto,
                                                   @PathVariable @Positive Long eventId,
                                                   @PathVariable @Positive Long commentId) {

        log.info("PATCH /admin/events/{eventId}/comments/{} — обновление комментария к событию с id={}",
                commentId, eventId);
        return adminCommentService.updateCommentByAdmin(updateCommentDto, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable @Positive Long eventId,
                                     @PathVariable @Positive Long commentId) {

        log.info("DELETE /admin/events/{eventId}/comments/{} — удаление комментария администратором", commentId);
        adminCommentService.deleteCommentByAdmin(eventId, commentId);
    }
}