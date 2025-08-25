package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

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

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EventInvitation> bulkCreate(@RequestBody List<CreateInvitationRequest> requests) {
        List<EventInvitation> all = new ArrayList<>();
        if (requests != null) {
            for (CreateInvitationRequest r : requests) {
                all.addAll(service.createInvitations(r.name(), r.phone(), r.eventIds()));
            }
        }
        return all;
    }
}
