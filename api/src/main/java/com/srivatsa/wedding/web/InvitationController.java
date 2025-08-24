package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.service.InvitationService;
import com.srivatsa.wedding.web.dto.EventInvitationDto;
import com.srivatsa.wedding.web.mapper.EntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/invitations")
public class InvitationController {

    private final InvitationService service;
    private final EntityMapper mapper;

    public InvitationController(InvitationService service, EntityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EventInvitationDto> list(@PathVariable Integer eventId) {
        return service.listByEvent(eventId).stream().map(mapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventInvitationDto invite(@PathVariable Integer eventId, @RequestBody EventInvitationDto dto) {
        EventInvitation inv = service.invite(eventId, dto.getGuestId(), dto.getInvited());
        return mapper.toDto(inv);
    }
}
