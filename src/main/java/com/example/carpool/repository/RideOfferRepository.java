package com.example.carpool.repository;

import com.example.carpool.model.RideOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {

    List<RideOffer> findAllByOrderByDepartureTimeAsc();
}
