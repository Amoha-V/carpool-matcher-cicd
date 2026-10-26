package com.example.carpool.repository;

import com.example.carpool.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByOfferId(Long offerId);

    boolean existsByOfferIdAndRiderNameIgnoreCase(Long offerId, String riderName);
}
