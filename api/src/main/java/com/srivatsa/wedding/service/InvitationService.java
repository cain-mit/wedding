package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.repository.EventInvitationRepository;
import com.srivatsa.wedding.repository.EventRepository;
import com.srivatsa.wedding.repository.GuestRepository;
import com.srivatsa.wedding.repository.RsvpRepository;
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
    private final RsvpRepository rsvpRepo;
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no confusing chars
    private static final SecureRandom RANDOM = new SecureRandom();

    public InvitationService(EventInvitationRepository repo, EventRepository eventRepo, GuestRepository guestRepo, RsvpRepository rsvpRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.guestRepo = guestRepo;
        this.rsvpRepo = rsvpRepo;
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
    public List<EventInvitation> createInvitations(String name, String phone, String lodgingName, List<Integer> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) throw new IllegalArgumentException("At least one event required");
        final Guest guest = guestRepo.save(Guest.builder()
                .name(name)
                .phone(phone)
                .lodgingName(lodgingName)
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

    public List<Guest> getPendingInvitations() {
        return repo.findGuestsWithPendingInvitations();
    }

    public String findInviteCodeByPhone(String phone) {
        return guestRepo.findByPhone(phone)
                .map(Guest::getInviteCode)
                .orElse(null);
    }

    public List<java.util.Map<String, Object>> getGuestSummary() {
        List<Guest> allGuests = guestRepo.findAll();
        
        return allGuests.stream()
                .map(this::buildGuestSummary)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private java.util.Map<String, Object> buildGuestSummary(Guest guest) {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        
        // Basic guest info
        summary.put("guestId", guest.getId());
        summary.put("name", guest.getName());
        summary.put("phone", guest.getPhone());
        summary.put("inviteCode", guest.getInviteCode());
        
        // Get events invited to
        List<EventInvitation> invitations = repo.findByGuest(guest);
        List<String> eventNames = invitations.stream()
                .map(inv -> inv.getEvent().getName())
                .collect(java.util.stream.Collectors.toList());
        summary.put("eventsInvitedTo", String.join(", ", eventNames));
        
        // Invitation sent status (assuming all invitations are sent together)
        boolean invitationSent = invitations.isEmpty() ? false : 
                invitations.stream().anyMatch(inv -> Boolean.TRUE.equals(inv.getInvited()));
        summary.put("invitationSent", invitationSent);
        
        // RSVP received status - check if there are any RSVPs for this guest
        List<com.srivatsa.wedding.domain.Rsvp> rsvps = rsvpRepo.findByGuest(guest);
        boolean rsvpReceived = !rsvps.isEmpty();
        summary.put("rsvpReceived", rsvpReceived ? "yes" : "no");
        
        // RSVP responded date - get the latest response date
        java.time.OffsetDateTime latestResponseDate = rsvps.stream()
                .map(com.srivatsa.wedding.domain.Rsvp::getRespondedAt)
                .filter(java.util.Objects::nonNull)
                .max(java.time.OffsetDateTime::compareTo)
                .orElse(null);
        summary.put("rsvpRespondedDate", latestResponseDate);
        
        // Generate invite text
        String inviteText = generateInviteText(guest, invitations);
        summary.put("inviteText", inviteText);
        
        return summary;
    }
    
    private String generateInviteText(Guest guest, List<EventInvitation> invitations) {
        String firstName = getFirstName(guest.getName());
        
        // Find earliest event date
        java.time.OffsetDateTime earliestDate = invitations.stream()
                .map(inv -> inv.getEvent().getStartTs())
                .filter(java.util.Objects::nonNull)
                .min(java.time.OffsetDateTime::compareTo)
                .orElse(null);
        
        String startDate = "28th November";
        if (earliestDate != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d");
            String day = earliestDate.format(formatter);
            String suffix = getDaySuffix(Integer.parseInt(day));
            java.time.format.DateTimeFormatter monthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM");
            String month = earliestDate.format(monthFormatter);
            startDate = day + suffix + " " + month;
        }
        
        return "*Hi " + firstName + ",*\n\nBig news! We, Srivatsa and Yamini, are tying the knot! \n\nWe would be overjoyed to have you celebrate with us as we begin our new journey together.\n\n*Dates:* " + startDate + " - 1st December 2025\n*City:* Jamshedpur\n\nFor the full schedule, travel details, and to keep up with all our updates, please visit our wedding website. It's also where you can *RSVP*!\n\n*Our Website:* https://wedding.srivatsa.dev?inviteCode=" + guest.getInviteCode() + "\n*Your Invite Code:* " + guest.getInviteCode() + "\n\nWe've also created a WhatsApp group for friends for easy communication and to share in the excitement. Feel free to join!\nhttps://tinyurl.com/sywafg\n\nHope to celebrate with you soon!";
    }
    
    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd"; 
            case 3: return "rd";
            default: return "th";
        }
    }
    
    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "there";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        return nameParts[0];
    }

    @Transactional
    public void markAllInvitationsSent(Integer guestId) {
        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found"));
        
        List<EventInvitation> invitations = repo.findByGuest(guest);
        
        for (EventInvitation invitation : invitations) {
            invitation.setInvited(true);
        }
        
        repo.saveAll(invitations);
    }

    @Transactional
    public void delete(Integer id) { repo.delete(repo.findById(id).orElseThrow(() -> new NotFoundException("Invitation not found"))); }
}
