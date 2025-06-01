package ru.practicum.ewm.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentFullAdminDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentNewDto;
import ru.practicum.ewm.dto.comment.CommentUpdateDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.CommentMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.event.EventRepository;
import ru.practicum.ewm.service.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentFullDto> getAllCommentsForEvent(Long eventId) {
        List<Comment> comments = commentRepository.findAllByEventIdAndNotDeletedOrderByCreatedAsc(eventId);

        return comments.stream()
                .map(CommentMapper::toCommentFullDto)
                .toList();
    }

    @Override
    public List<CommentFullAdminDto> getAllCommentsByAdminForEvent(Long eventId) {
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedAsc(eventId);

        return comments.stream()
                .map(CommentMapper::toCommentFullAdminDto)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        if (comment.getDeleted()) {
            throw new ConflictException("Комментарий с id = " + commentId + " уже удален.");
        }

        comment.setDeleted(true);

        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = getUserById(userId);
        Comment comment = getCommentById(commentId);
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("Пользователь с id = " + userId + " не является автором комментария с id = " + commentId);
        }

        deleteComment(commentId);
    }

    @Override
    public CommentFullDto createComment(Long eventId, Long userId, CommentNewDto commentNewDto) {
        Comment existComment = commentRepository.findByEventIdAndAuthorId(eventId, userId);
        if (existComment != null) {
            throw new ConflictException("Комментарий от пользователя с id = " + userId + " к событию с id = " + eventId + " уже существует.");
        }

        User user = getUserById(userId);
        Event event = getEventById(eventId);

        Comment comment = Comment.builder()
                .text(commentNewDto.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .deleted(false)
                .build();

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto updateComment(Long userId, Long commentId, CommentUpdateDto commentUpdateDto) {
        User user = getUserById(userId);
        Comment comment = getCommentById(commentId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("Пользователь с id = " + userId + " не является автором комментария с id = " + commentId);
        }

        if (comment.getCreated().minusHours(2).isAfter(LocalDateTime.now())) {
            throw new ConflictException("Нельзя изменить комментарий, который был создан более 2 часов назад.");
        }

        if (commentUpdateDto.getText() != null) {
            comment.setText(commentUpdateDto.getText());
        }

        //Потому что пытались изменить
        comment.setEdited(LocalDateTime.now());

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto getComment(Long commentId) {
        return CommentMapper.toCommentFullDto(getCommentById(commentId));
    }

    @Override
    public CommentFullAdminDto getCommentByAdmin(Long commentId) {
        return CommentMapper.toCommentFullAdminDto(getCommentById(commentId, true));
    }

    private Comment getCommentById(Long commentId) {
        return getCommentById(commentId, false);
    }

    private Comment getCommentById(Long commentId, Boolean all) {
        if (!all) {
            Comment comment = commentRepository.findByIdAndNotDeleted(commentId);
            if (comment == null) {
                throw new NotFoundException("Комментарий с id = " + commentId + " не найден.");
            }

            return comment;
        } else {
            return commentRepository.findById(commentId).orElseThrow(
                    () -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }
}
