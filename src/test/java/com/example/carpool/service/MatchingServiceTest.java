package com.example.carpool.service;

import com.example.carpool.model.Place;
import com.example.carpool.model.RideOffer;
import com.example.carpool.repository.RideOfferRepository;
import com.example.carpool.web.MatchRequest;
import com.example.carpool.web.MatchResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MatchingServiceTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 24, 7, 45);

    private final MatchingService service = new MatchingService(mock(RideOfferRepository.class));

    private RideOffer offer(long id, Place from, Place to, LocalDateTime time, int seatsAvailable) {
        RideOffer o = new RideOffer("Driver" + id, from, to, time, 4);
        o.setId(id);
        o.setSeatsAvailable(seatsAvailable);
        return o;
    }

    @Test
    void filtersOutFullFarAndLateOffersAndRanksTheRest() {
        List<RideOffer> offers = List.of(
                offer(1, Place.TAMBARAM, Place.SNU_CHENNAI, BASE, 2),                    // perfect
                offer(2, Place.TAMBARAM, Place.SNU_CHENNAI, BASE, 0),                    // no seats
                offer(3, Place.VELACHERY, Place.SNU_CHENNAI, BASE, 2),                   // pickup too far
                offer(4, Place.TAMBARAM, Place.SNU_CHENNAI, BASE.plusMinutes(105), 2),   // outside time window
                offer(5, Place.TAMBARAM, Place.SNU_CHENNAI, BASE.plusMinutes(15), 1));   // slightly later

        MatchRequest request = new MatchRequest(Place.TAMBARAM, Place.SNU_CHENNAI, BASE, 30);
        List<MatchResult> results = service.rank(request, offers);

        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).offerId());
        assertEquals(100.0, results.get(0).score(), 0.01);
        assertEquals(5L, results.get(1).offerId());
        assertEquals(92.5, results.get(1).score(), 0.01);
        assertEquals(15, results.get(1).timeDiffMinutes());
    }

    @Test
    void nearbyDropOffIsAcceptedWithLowerScore() {
        List<RideOffer> offers = List.of(offer(1, Place.TAMBARAM, Place.SIRUSERI, BASE, 3));

        MatchRequest request = new MatchRequest(Place.TAMBARAM, Place.NAVALUR, BASE, 0);
        List<MatchResult> results = service.rank(request, offers);

        assertEquals(1, results.size());
        MatchResult r = results.get(0);
        assertTrue(r.dropKm() > 1.5 && r.dropKm() < 2.5, "dropKm was " + r.dropKm());
        assertTrue(r.score() < 100.0 && r.score() > 70.0, "score was " + r.score());
    }

    @Test
    void returnsEmptyListWhenNothingMatches() {
        List<RideOffer> offers = List.of(offer(1, Place.ANNA_NAGAR, Place.CHENNAI_CENTRAL, BASE, 3));

        MatchRequest request = new MatchRequest(Place.TAMBARAM, Place.SNU_CHENNAI, BASE, 60);

        assertTrue(service.rank(request, offers).isEmpty());
    }
}
