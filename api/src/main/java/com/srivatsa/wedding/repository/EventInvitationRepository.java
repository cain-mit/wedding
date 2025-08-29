package com.srivatsa.wedding.repository;

import com.srivatsa.wedding.domain.EventInvitation;
import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, Integer> {
    Optional<EventInvitation> findByEventAndGuest(Event event, Guest guest);
    boolean existsByEventIdAndGuestId(Integer eventId, Integer guestId);
    List<EventInvitation> findByGuest(Guest guest);
    
    @Query("SELECT DISTINCT g FROM Guest g JOIN g.invitations i WHERE i.invited = false")
    List<Guest> findGuestsWithPendingInvitations();
}
