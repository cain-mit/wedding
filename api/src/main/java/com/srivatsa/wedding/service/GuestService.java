package com.srivatsa.wedding.service;

import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.domain.GuestDetails;
import com.srivatsa.wedding.repository.GuestDetailsRepository;
import com.srivatsa.wedding.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GuestService {
    private final GuestRepository guestRepo;
    private final GuestDetailsRepository detailsRepo;

    public GuestService(GuestRepository guestRepo, GuestDetailsRepository detailsRepo) {
        this.guestRepo = guestRepo;
        this.detailsRepo = detailsRepo;
    }

    public List<Guest> findAll() { return guestRepo.findAll(); }
    public Guest findById(Integer id) { return guestRepo.findById(id).orElseThrow(() -> new NotFoundException("Guest not found")); }
    public Guest findByInviteCode(String inviteCode) { return guestRepo.findByInviteCode(inviteCode).orElseThrow(() -> new NotFoundException("Guest not found")); }

    @Transactional
    public Guest create(Guest g) { return guestRepo.save(g); }

    @Transactional
    public Guest update(Integer id, Guest updated) {
        Guest existing = findById(id);
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setInviteCode(updated.getInviteCode());
        existing.setPartyId(updated.getPartyId());
        existing.setLodgingName(updated.getLodgingName());
        return existing;
    }

    @Transactional
    public Guest partialUpdate(Guest guest, Guest partial) {
        if (partial.getName() != null) guest.setName(partial.getName());
        if (partial.getPhone() != null) guest.setPhone(partial.getPhone());
        if (partial.getEmail() != null) guest.setEmail(partial.getEmail());
        if (partial.getInviteCode() != null) guest.setInviteCode(partial.getInviteCode());
        if (partial.getPartyId() != null) guest.setPartyId(partial.getPartyId());
        if (partial.getLodgingName() != null) guest.setLodgingName(partial.getLodgingName());
        return guest;
    }

    @Transactional
    public void delete(Integer id) { guestRepo.delete(findById(id)); }

    public GuestDetails getDetails(Integer guestId) {
        Guest guest = findById(guestId);
        return guest.getDetails();
    }

    @Transactional
    public GuestDetails upsertDetails(Integer guestId, GuestDetails details) {
        Guest guest = findById(guestId);
        GuestDetails existing = guest.getDetails();
        if (existing == null) {
            details.setGuest(guest);
            return detailsRepo.save(details);
        } else {
            existing.setArrivalTs(details.getArrivalTs());
            existing.setDepartureTs(details.getDepartureTs());
            existing.setArrivalMethod(details.getArrivalMethod());
            existing.setDepartureMethod(details.getDepartureMethod());
            existing.setArrivalFlightNumber(details.getArrivalFlightNumber());
            existing.setDepartureFlightNumber(details.getDepartureFlightNumber());
            existing.setArrivalAirport(details.getArrivalAirport());
            existing.setDepartureAirport(details.getDepartureAirport());
            existing.setNotes(details.getNotes());
            existing.setUpdatedAt(details.getUpdatedAt());
            return existing;
        }
    }

    @Transactional
    public GuestDetails partialUpsertDetails(Guest guest, GuestDetails partial) {
        if (partial == null) return guest.getDetails();
        GuestDetails existing = guest.getDetails();
        if (existing == null) {
            partial.setGuest(guest);
            return detailsRepo.save(partial);
        }
        if (partial.getArrivalTs() != null) existing.setArrivalTs(partial.getArrivalTs());
        if (partial.getDepartureTs() != null) existing.setDepartureTs(partial.getDepartureTs());
        if (partial.getArrivalMethod() != null) existing.setArrivalMethod(partial.getArrivalMethod());
        if (partial.getDepartureMethod() != null) existing.setDepartureMethod(partial.getDepartureMethod());
        if (partial.getArrivalFlightNumber() != null) existing.setArrivalFlightNumber(partial.getArrivalFlightNumber());
        if (partial.getDepartureFlightNumber() != null) existing.setDepartureFlightNumber(partial.getDepartureFlightNumber());
        if (partial.getArrivalAirport() != null) existing.setArrivalAirport(partial.getArrivalAirport());
        if (partial.getDepartureAirport() != null) existing.setDepartureAirport(partial.getDepartureAirport());
        if (partial.getNotes() != null) existing.setNotes(partial.getNotes());
        if (partial.getUpdatedAt() != null) existing.setUpdatedAt(partial.getUpdatedAt());
        return existing;
    }
}
