package ru.practicum.ewm.service.comment;

import jakarta.validation.Valid;
import ru.practicum.ewm.dto.comment.CommentFullAdminDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentNewDto;
import ru.practicum.ewm.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    List<CommentFullDto> getAllCommentsForEvent(Long eventId);

    void deleteComment(Long commentId);

    void deleteComment(Long userId, Long commentId);

    CommentFullDto createComment(Long eventId, Long userId, @Valid CommentNewDto commentNewDto);

    CommentFullDto updateComment(Long userId, Long commentId, @Valid CommentUpdateDto commentUpdateDto);

    CommentFullDto getComment(Long commentId);

    List<CommentFullAdminDto> getAllCommentsByAdminForEvent(Long eventId);

    CommentFullAdminDto getCommentByAdmin(Long commentId);
}
