package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PubCommentController {
    private final CommentService commentService;

    @GetMapping("/comment/{commentId}")
    public CommentFullDto getById(@PathVariable("commentId") Long commentId) {
        return commentService.getComment(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentFullDto> getAllCommentsForEvent(@PathVariable("eventId") Long eventId) {
        return commentService.getAllCommentsForEvent(eventId);
    }
}
