package com.example.carpool.web;

import com.example.carpool.model.Booking;
import com.example.carpool.model.Place;
import com.example.carpool.model.RideOffer;
import com.example.carpool.service.MatchingService;
import com.example.carpool.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CarpoolController {

    private final RideService rideService;
    private final MatchingService matchingService;

    public CarpoolController(RideService rideService, MatchingService matchingService) {
        this.rideService = rideService;
        this.matchingService = matchingService;
    }

    @GetMapping("/places")
    public List<Map<String, String>> places() {
        return Arrays.stream(Place.values())
                .map(p -> Map.of("code", p.name(), "label", p.getLabel()))
                .toList();
    }

    @GetMapping("/offers")
    public List<RideOffer> offers() {
        return rideService.listOffers();
    }

    @PostMapping("/offers")
    @ResponseStatus(HttpStatus.CREATED)
    public RideOffer createOffer(@Valid @RequestBody RideOffer offer) {
        return rideService.createOffer(offer);
    }

    @PostMapping("/offers/{id}/book")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking book(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return rideService.book(id, request.riderName());
    }

    @GetMapping("/offers/{id}/bookings")
    public List<Booking> bookings(@PathVariable Long id) {
        return rideService.bookingsFor(id);
    }

    @PostMapping("/matches")
    public List<MatchResult> matches(@Valid @RequestBody MatchRequest request) {
        return matchingService.findMatches(request);
    }
}
