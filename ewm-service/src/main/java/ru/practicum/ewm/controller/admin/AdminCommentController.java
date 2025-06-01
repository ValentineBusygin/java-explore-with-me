package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentFullAdminDto;
import ru.practicum.ewm.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentFullAdminDto> getAllCommentsByEventId(@PathVariable Long eventId) {
        return commentService.getAllCommentsByAdminForEvent(eventId);
    }

    @GetMapping("/comment/{commentId}")
    public CommentFullAdminDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentByAdmin(commentId);
    }
}
