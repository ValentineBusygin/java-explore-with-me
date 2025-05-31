package ru.practicum.ewm.service.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    public List<Event> findAllByCategoryId(Long categoryId);

    //Page<Event> findAll(Specification<Event> spec, PageRequest pageRequest);
    Page<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);
}
