package com.srivatsa.wedding.repository;

import com.srivatsa.wedding.domain.Rsvp;
import com.srivatsa.wedding.domain.Event;
import com.srivatsa.wedding.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RsvpRepository extends JpaRepository<Rsvp, Integer> {
    Optional<Rsvp> findByEventAndGuest(Event event, Guest guest);
    List<Rsvp> findByGuest(Guest guest);
}
