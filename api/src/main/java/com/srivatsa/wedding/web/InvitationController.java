package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService service;

    public InvitationController(InvitationService service) {
        this.service = service;
    }

    public record CreateInvitationRequest(String name, String phone, List<Integer> eventIds) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<EventInvitation> create(@RequestBody CreateInvitationRequest request) {
        return service.createInvitations(request.name(), request.phone(), request.eventIds());
    }
}
