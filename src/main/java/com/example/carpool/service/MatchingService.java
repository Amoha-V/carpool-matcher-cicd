package com.example.carpool.service;

import com.example.carpool.model.RideOffer;
import com.example.carpool.repository.RideOfferRepository;
import com.example.carpool.web.MatchRequest;
import com.example.carpool.web.MatchResult;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ranks ride offers for a rider.
 *
 * An offer is a candidate when it still has a free seat, its start point is within
 * MAX_PICKUP_KM of the rider's origin, its end point is within MAX_DROP_KM of the
 * rider's destination, and it leaves within the rider's time flexibility.
 *
 * score = max(0, 100 - 8*pickupKm - 8*dropKm - 0.5*minutesFromPreferredTime)
 */
@Service
public class MatchingService {

    static final double MAX_PICKUP_KM = 6.0;
    static final double MAX_DROP_KM = 6.0;
    static final double DISTANCE_PENALTY_PER_KM = 8.0;
    static final double TIME_PENALTY_PER_MINUTE = 0.5;

    private final RideOfferRepository repository;

    public MatchingService(RideOfferRepository repository) {
        this.repository = repository;
    }

    public List<MatchResult> findMatches(MatchRequest request) {
        return rank(request, repository.findAll());
    }

    /** Pure ranking logic (no database access), which keeps it easy to unit test. */
    public List<MatchResult> rank(MatchRequest request, List<RideOffer> offers) {
        List<MatchResult> results = new ArrayList<>();

        for (RideOffer offer : offers) {
            if (offer.getSeatsAvailable() <= 0) {
                continue;
            }
            double pickupKm = request.origin().distanceKm(offer.getOrigin());
            double dropKm = request.destination().distanceKm(offer.getDestination());
            long diffMinutes = Math.abs(
                    Duration.between(request.departureTime(), offer.getDepartureTime()).toMinutes());

            if (pickupKm > MAX_PICKUP_KM || dropKm > MAX_DROP_KM || diffMinutes > request.flexMinutes()) {
                continue;
            }

            double score = Math.max(0, 100
                    - DISTANCE_PENALTY_PER_KM * pickupKm
                    - DISTANCE_PENALTY_PER_KM * dropKm
                    - TIME_PENALTY_PER_MINUTE * diffMinutes);

            results.add(new MatchResult(
                    offer.getId(), offer.getDriverName(), offer.getOrigin(), offer.getDestination(),
                    offer.getDepartureTime(), offer.getSeatsAvailable(),
                    round1(pickupKm), round1(dropKm), diffMinutes, round1(score)));
        }

        results.sort(Comparator.comparingDouble(MatchResult::score).reversed()
                .thenComparing(MatchResult::departureTime));
        return results;
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
