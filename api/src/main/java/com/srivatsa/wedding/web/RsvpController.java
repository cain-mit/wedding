package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Rsvp;
import com.srivatsa.wedding.service.RsvpService;
import com.srivatsa.wedding.web.dto.RsvpDto;
import com.srivatsa.wedding.web.mapper.EntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/rsvps")
public class RsvpController {

    private final RsvpService service;
    private final EntityMapper mapper;

    public RsvpController(RsvpService service, EntityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RsvpDto> list(@PathVariable Integer eventId) {
        return service.listByEvent(eventId).stream().map(mapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsvpDto respond(@PathVariable Integer eventId, @RequestBody RsvpDto dto) {
        Rsvp r = service.respond(eventId, dto.getGuestId(), dto.getStatus(), dto.getDietary(), dto.getComment());
        return mapper.toDto(r);
    }
}
