package com.srivatsa.wedding.repository;

import com.srivatsa.wedding.domain.GuestDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestDetailsRepository extends JpaRepository<GuestDetails, Integer> {
}
