package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.*;
import com.srivatsa.wedding.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public record RsvpFormResult(Guest guest, GuestDetails details, Rsvp rsvp) {}

    @Transactional
    public RsvpFormResult respondWithGuestUpdate(Integer eventId, String inviteCode, String status, String dietary, String comment, Guest guestUpdates, GuestDetails detailsUpdates) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Guest guest = guestRepo.findByInviteCode(inviteCode).orElseThrow(() -> new NotFoundException("Guest not found"));
        if (guestUpdates != null) {
            if (guestUpdates.getName() != null) guest.setName(guestUpdates.getName());
            if (guestUpdates.getPhone() != null) guest.setPhone(guestUpdates.getPhone());
            if (guestUpdates.getEmail() != null) guest.setEmail(guestUpdates.getEmail());
            if (guestUpdates.getLodgingName() != null) guest.setLodgingName(guestUpdates.getLodgingName());
        }
        // guest details
        if (detailsUpdates != null) {
            GuestDetails existing = guest.getDetails();
            if (existing == null) {
                detailsUpdates.setGuest(guest);
                guest.setDetails(detailsUpdates);
            } else {
                if (detailsUpdates.getArrivalTs() != null) existing.setArrivalTs(detailsUpdates.getArrivalTs());
                if (detailsUpdates.getDepartureTs() != null) existing.setDepartureTs(detailsUpdates.getDepartureTs());
                if (detailsUpdates.getArrivalMethod() != null) existing.setArrivalMethod(detailsUpdates.getArrivalMethod());
                if (detailsUpdates.getDepartureMethod() != null) existing.setDepartureMethod(detailsUpdates.getDepartureMethod());
                if (detailsUpdates.getArrivalFlightNumber() != null) existing.setArrivalFlightNumber(detailsUpdates.getArrivalFlightNumber());
                if (detailsUpdates.getDepartureFlightNumber() != null) existing.setDepartureFlightNumber(detailsUpdates.getDepartureFlightNumber());
                if (detailsUpdates.getArrivalAirport() != null) existing.setArrivalAirport(detailsUpdates.getArrivalAirport());
                if (detailsUpdates.getDepartureAirport() != null) existing.setDepartureAirport(detailsUpdates.getDepartureAirport());
                if (detailsUpdates.getNotes() != null) existing.setNotes(detailsUpdates.getNotes());
                existing.setUpdatedAt(OffsetDateTime.now());
            }
        }
        // ensure invited
        if(!invitationRepo.existsByEventIdAndGuestId(eventId, guest.getId())) {
            throw new IllegalStateException("Guest not invited to event");
        }
        OffsetDateTime now = OffsetDateTime.now();
        Rsvp rsvp = repo.findByEventAndGuest(event, guest).map(r -> {
            r.setStatus(status);
            r.setDietary(dietary);
            r.setComment(comment);
            r.setRespondedAt(now);
            return r;
        }).orElseGet(() -> repo.save(Rsvp.builder().event(event).guest(guest).status(status).dietary(dietary).comment(comment).respondedAt(now).build()));
        return new RsvpFormResult(guest, guest.getDetails(), rsvp);
    }

    public record RsvpFormMultiResult(Guest guest, GuestDetails details, List<Rsvp> rsvps) {}

    @Transactional
    public RsvpFormMultiResult respondForMultiple(Integer[] eventIds, String inviteCode, Map<Integer,String> statuses, String dietary, String comment, Guest guestUpdates, GuestDetails detailsUpdates) {
        Guest guest = guestRepo.findByInviteCode(inviteCode).orElseThrow(() -> new NotFoundException("Guest not found"));
        if (guestUpdates != null) {
            if (guestUpdates.getName() != null) guest.setName(guestUpdates.getName());
            if (guestUpdates.getPhone() != null) guest.setPhone(guestUpdates.getPhone());
            if (guestUpdates.getEmail() != null) guest.setEmail(guestUpdates.getEmail());
            if (guestUpdates.getLodgingName() != null) guest.setLodgingName(guestUpdates.getLodgingName());
        }
        if (detailsUpdates != null) {
            GuestDetails existing = guest.getDetails();
            if (existing == null) {
                detailsUpdates.setGuest(guest);
                guest.setDetails(detailsUpdates);
            } else {
                if (detailsUpdates.getArrivalTs() != null) existing.setArrivalTs(detailsUpdates.getArrivalTs());
                if (detailsUpdates.getDepartureTs() != null) existing.setDepartureTs(detailsUpdates.getDepartureTs());
                if (detailsUpdates.getArrivalMethod() != null) existing.setArrivalMethod(detailsUpdates.getArrivalMethod());
                if (detailsUpdates.getDepartureMethod() != null) existing.setDepartureMethod(detailsUpdates.getDepartureMethod());
                if (detailsUpdates.getArrivalFlightNumber() != null) existing.setArrivalFlightNumber(detailsUpdates.getArrivalFlightNumber());
                if (detailsUpdates.getDepartureFlightNumber() != null) existing.setDepartureFlightNumber(detailsUpdates.getDepartureFlightNumber());
                if (detailsUpdates.getArrivalAirport() != null) existing.setArrivalAirport(detailsUpdates.getArrivalAirport());
                if (detailsUpdates.getDepartureAirport() != null) existing.setDepartureAirport(detailsUpdates.getDepartureAirport());
                if (detailsUpdates.getNotes() != null) existing.setNotes(detailsUpdates.getNotes());
                existing.setUpdatedAt(OffsetDateTime.now());
            }
        }
        OffsetDateTime now = OffsetDateTime.now();
        List<Rsvp> rsvps = new ArrayList<>();
        for (Map.Entry<Integer,String> entry : statuses.entrySet()) {
            Integer eventId = entry.getKey();
            String status = entry.getValue();
            Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
            if(!invitationRepo.existsByEventIdAndGuestId(eventId, guest.getId())) {
                continue; // skip not invited
            }
            Rsvp rsvp = repo.findByEventAndGuest(event, guest).map(r -> {
                r.setStatus(status);
                r.setDietary(dietary);
                r.setComment(comment);
                r.setRespondedAt(now);
                return r;
            }).orElseGet(() -> repo.save(Rsvp.builder().event(event).guest(guest).status(status).dietary(dietary).comment(comment).respondedAt(now).build()));
            rsvps.add(rsvp);
        }
        return new RsvpFormMultiResult(guest, guest.getDetails(), rsvps);
    }

    public RsvpFormMultiResult getForm(String inviteCode) {
        Guest guest = guestRepo.findByInviteCode(inviteCode).orElseThrow(() -> new NotFoundException("Guest not found"));
        List<Rsvp> rsvps = guest.getRsvps();
        return new RsvpFormMultiResult(guest, guest.getDetails(), rsvps);
    }
}
