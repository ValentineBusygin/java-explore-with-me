package ru.practicum.ewm.service.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByEventIdAndAuthorId(Long eventId, Long userId);

    List<Comment> findAllByEventIdOrderByCreatedAsc(Long eventId);

    @Query("select c from Comment c where c.event.id = ?1 and c.deleted = false")
    List<Comment> findAllByEventIdAndNotDeletedOrderByCreatedAsc(Long eventId);

    @Query("select c from Comment c where c.id = ?1 and c.deleted = false")
    Comment findByIdAndNotDeleted(Long commentId);

    List<Comment> findAllByEventIdIn(List<Long> eventIds);
}
