package com.example.carpool.web;

import com.example.carpool.model.Place;

import java.time.LocalDateTime;

/** One ranked suggestion returned to a rider. */
public record MatchResult(
        Long offerId,
        String driverName,
        Place origin,
        Place destination,
        LocalDateTime departureTime,
        int seatsAvailable,
        double pickupKm,
        double dropKm,
        long timeDiffMinutes,
        double score) {
}
