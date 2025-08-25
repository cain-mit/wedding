package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.repository.EventInvitationRepository;
import com.srivatsa.wedding.repository.EventRepository;
import com.srivatsa.wedding.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvitationService {
    private final EventInvitationRepository repo;
    private final EventRepository eventRepo;
    private final GuestRepository guestRepo;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no confusing chars
    private static final SecureRandom RANDOM = new SecureRandom();

    public InvitationService(EventInvitationRepository repo, EventRepository eventRepo, GuestRepository guestRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.guestRepo = guestRepo;
    }

    public List<EventInvitation> listByEvent(Integer eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        return event.getInvitations();
    }

    private String generateInviteCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            code = sb.toString();
        } while (guestRepo.findByInviteCode(code).isPresent());
        return code;
    }

    @Transactional
    public List<EventInvitation> createInvitations(String name, String phone, List<Integer> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) throw new IllegalArgumentException("At least one event required");
        final Guest guest = guestRepo.save(Guest.builder()
                .name(name)
                .phone(phone)
                .inviteCode(generateInviteCode())
                .build());
        for (Integer eventId : eventIds) {
            Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
            repo.findByEventAndGuest(event, guest).orElseGet(() -> repo.save(EventInvitation.builder().event(event).guest(guest).invited(false).build()));
        }
        return guest.getInvitations();
    }

    @Transactional
    public EventInvitation invite(Integer eventId, Integer guestId, Boolean invited) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Guest guest = guestRepo.findById(guestId).orElseThrow(() -> new NotFoundException("Guest not found"));
        return repo.findByEventAndGuest(event, guest).map(inv -> {
            inv.setInvited(invited);
            return inv;
        }).orElseGet(() -> repo.save(EventInvitation.builder().event(event).guest(guest).invited(invited).build()));
    }

    @Transactional
    public void delete(Integer id) { repo.delete(repo.findById(id).orElseThrow(() -> new NotFoundException("Invitation not found"))); }
}
