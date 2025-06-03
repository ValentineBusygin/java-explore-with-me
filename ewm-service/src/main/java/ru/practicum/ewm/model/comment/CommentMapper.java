package ru.practicum.ewm.model.comment;

import ru.practicum.ewm.dto.comment.CommentFullAdminDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.model.event.EventMapper;
import ru.practicum.ewm.model.user.UserMapper;

public class CommentMapper {
    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated())
                .edited(comment.getEdited())
                .build();
    }

    public static CommentFullAdminDto toCommentFullAdminDto(Comment comment) {
        return CommentFullAdminDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated())
                .edited(comment.getEdited())
                .deleted(comment.getDeleted())
                .build();
    }
}
