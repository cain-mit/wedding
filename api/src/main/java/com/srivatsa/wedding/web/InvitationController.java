package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public record CreateInvitationRequest(String name, String phone, String lodgingName, List<Integer> eventIds) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<EventInvitation> create(@RequestBody CreateInvitationRequest request) {
        return service.createInvitations(request.name(), request.phone(), request.lodgingName(), request.eventIds());
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<EventInvitation>> bulkCreate(
            @RequestParam("authCode") String authCode,
            @RequestBody List<CreateInvitationRequest> requests) {
        
        if (!"ILOVEYAMINI".equals(authCode)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<EventInvitation> all = new ArrayList<>();
        if (requests != null && !requests.isEmpty()) {
            for (CreateInvitationRequest r : requests) {
                List<EventInvitation> created = service.createInvitations(r.name(), r.phone(), r.lodgingName(), r.eventIds());
                all.addAll(created);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(all);
    }

    @GetMapping("/pending")
    public List<Guest> getPendingInvitations() {
        return service.getPendingInvitations();
    }

    @GetMapping("/inviteCode")
    public ResponseEntity<java.util.Map<String, String>> getInviteCodeByPhone(@RequestParam String phone) {
        String phoneWithPlus = "+" + phone;
        String inviteCode = service.findInviteCodeByPhone(phoneWithPlus);
        
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("recipient", phone);
        
        if (inviteCode != null) {
            response.put("responseText", "Here's your invite code: *" + inviteCode + "*");
        } else {
            response.put("responseText", "Sorry, we couldn't found your invite code :( Please contact Srivatsa or Yamini");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guest-summary")
    public ResponseEntity<List<java.util.Map<String, Object>>> getGuestSummary(@RequestParam("authCode") String authCode) {
        if (!"ILOVEYAMINI".equals(authCode)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(service.getGuestSummary());
    }

    @PostMapping("/mark-invited/{guestId}")
    public ResponseEntity<String> markInvitationsSent(
            @PathVariable Integer guestId) {
        try {
            service.markAllInvitationsSent(guestId);
            return ResponseEntity.ok("All invitations marked as sent for guest ID: " + guestId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guest not found or error occurred: " + e.getMessage());
        }
    }
}
