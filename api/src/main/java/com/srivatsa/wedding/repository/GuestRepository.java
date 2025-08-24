package com.srivatsa.wedding.repository;

import com.srivatsa.wedding.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
    Optional<Guest> findByInviteCode(String inviteCode);
}
