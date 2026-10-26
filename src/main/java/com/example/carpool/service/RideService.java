package com.example.carpool.service;

import com.example.carpool.model.Booking;
import com.example.carpool.model.RideOffer;
import com.example.carpool.repository.BookingRepository;
import com.example.carpool.repository.RideOfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RideService {

    private final RideOfferRepository offerRepository;
    private final BookingRepository bookingRepository;

    public RideService(RideOfferRepository offerRepository, BookingRepository bookingRepository) {
        this.offerRepository = offerRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<RideOffer> listOffers() {
        return offerRepository.findAllByOrderByDepartureTimeAsc();
    }

    public RideOffer createOffer(RideOffer offer) {
        if (offer.getOrigin() == offer.getDestination()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Origin and destination must be different");
        }
        offer.setId(null);
        offer.setDriverName(offer.getDriverName().trim());
        offer.setSeatsAvailable(offer.getSeatsTotal());
        return offerRepository.save(offer);
    }

    @Transactional
    public Booking book(Long offerId, String riderName) {
        String rider = riderName.trim();
        RideOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ride offer not found: " + offerId));

        if (offer.getDriverName().equalsIgnoreCase(rider)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A driver cannot book a seat on their own ride");
        }
        if (bookingRepository.existsByOfferIdAndRiderNameIgnoreCase(offerId, rider)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already booked this ride");
        }
        if (offer.getSeatsAvailable() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No seats left on this ride");
        }

        offer.setSeatsAvailable(offer.getSeatsAvailable() - 1);
        offerRepository.save(offer);
        return bookingRepository.save(new Booking(offerId, rider));
    }

    public List<Booking> bookingsFor(Long offerId) {
        return bookingRepository.findByOfferId(offerId);
    }
}
