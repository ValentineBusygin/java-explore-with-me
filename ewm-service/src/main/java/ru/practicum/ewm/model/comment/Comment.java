package ru.practicum.ewm.model.comment;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime created;

    private LocalDateTime edited;

    private Boolean deleted = false;

    private String text;

}
