package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Event get(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody Event event) { return service.create(event); }

    @PutMapping("/{id}")
    public Event update(@PathVariable Integer id, @RequestBody Event event) { return service.update(id, event); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
