package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentNewDto;
import ru.practicum.ewm.dto.comment.CommentUpdateDto;
import ru.practicum.ewm.service.comment.CommentService;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/event/{eventId}/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable("eventId") Long eventId,
                                        @PathVariable("userId") Long userId,
                                        @RequestBody @Valid CommentNewDto commentNewDto) {
        return commentService.createComment(eventId, userId, commentNewDto);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @PatchMapping("/user/{userId}/comment/{commentId}")
    public CommentFullDto updateComment(@PathVariable("userId") Long userId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        return commentService.updateComment(userId, commentId, commentUpdateDto);
    }


}
