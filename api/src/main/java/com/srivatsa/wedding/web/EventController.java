package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.service.EventService;
import com.srivatsa.wedding.web.dto.EventDto;
import com.srivatsa.wedding.web.mapper.EntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;
    private final EntityMapper mapper;

    public EventController(EventService service, EntityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EventDto> list() {
        return service.findAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable Integer id) { return mapper.toDto(service.findById(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody EventDto dto) { return mapper.toDto(service.create(mapper.toEntity(dto))); }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable Integer id, @RequestBody EventDto dto) { return mapper.toDto(service.update(id, mapper.toEntity(dto))); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
