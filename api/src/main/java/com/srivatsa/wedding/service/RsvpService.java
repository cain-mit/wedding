package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.*;
import com.srivatsa.wedding.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RsvpService {
    private final RsvpRepository repo;
    private final EventRepository eventRepo;
    private final GuestRepository guestRepo;
    private final EventInvitationRepository invitationRepo;

    public RsvpService(RsvpRepository repo, EventRepository eventRepo, GuestRepository guestRepo, EventInvitationRepository invitationRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.guestRepo = guestRepo;
        this.invitationRepo = invitationRepo;
    }

    public List<Rsvp> listByEvent(Integer eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        return event.getRsvps();
    }

    @Transactional
    public Rsvp respond(Integer eventId, Integer guestId, String status, String dietary, String comment) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Guest guest = guestRepo.findById(guestId).orElseThrow(() -> new NotFoundException("Guest not found"));
        // ensure invited
        if(!invitationRepo.existsByEventIdAndGuestId(eventId, guestId)) {
            throw new IllegalStateException("Guest not invited to event");
        }
        return repo.findByEventAndGuest(event, guest).map(r -> {
            r.setStatus(status);
            r.setDietary(dietary);
            r.setComment(comment);
            r.setRespondedAt(OffsetDateTime.now());
            return r;
        }).orElseGet(() -> repo.save(Rsvp.builder().event(event).guest(guest).status(status).dietary(dietary).comment(comment).respondedAt(OffsetDateTime.now()).build()));
    }
}
