package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<Event> findAll() { return repo.findAll(); }
    public Event findById(Integer id) { return repo.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); }

    @Transactional
    public Event create(Event e) { return repo.save(e); }

    @Transactional
    public Event update(Integer id, Event updated) {
        Event existing = findById(id);
        existing.setName(updated.getName());
        existing.setStartTs(updated.getStartTs());
        existing.setEndTs(updated.getEndTs());
        existing.setLocation(updated.getLocation());
        existing.setDescription(updated.getDescription());
        return existing;
    }

    @Transactional
    public void delete(Integer id) { repo.delete(findById(id)); }
}
