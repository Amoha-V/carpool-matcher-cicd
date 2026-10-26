package com.example.carpool.service;

import com.example.carpool.model.Booking;
import com.example.carpool.model.Place;
import com.example.carpool.model.RideOffer;
import com.example.carpool.repository.BookingRepository;
import com.example.carpool.repository.RideOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideOfferRepository offerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RideService service;

    private RideOffer sampleOffer(int seatsAvailable) {
        RideOffer o = new RideOffer("Arjun", Place.TAMBARAM, Place.SNU_CHENNAI,
                LocalDateTime.of(2026, 10, 24, 7, 45), 3);
        o.setId(1L);
        o.setSeatsAvailable(seatsAvailable);
        return o;
    }

    @Test
    void createOfferSetsAvailableSeatsToTotal() {
        when(offerRepository.save(any(RideOffer.class))).thenAnswer(inv -> inv.getArgument(0));

        RideOffer created = service.createOffer(new RideOffer("  Meera ", Place.VELACHERY,
                Place.SNU_CHENNAI, LocalDateTime.of(2026, 10, 24, 8, 0), 4));

        assertEquals(4, created.getSeatsAvailable());
        assertEquals("Meera", created.getDriverName());
    }

    @Test
    void createOfferRejectsSameOriginAndDestination() {
        RideOffer bad = new RideOffer("Meera", Place.ADYAR, Place.ADYAR,
                LocalDateTime.of(2026, 10, 24, 8, 0), 2);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.createOffer(bad));

        assertEquals(400, ex.getStatusCode().value());
        verifyNoInteractions(offerRepository);
    }

    @Test
    void bookingReducesAvailableSeats() {
        RideOffer offer = sampleOffer(2);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking booking = service.book(1L, "Amoha");

        assertEquals(1, offer.getSeatsAvailable());
        assertEquals("Amoha", booking.getRiderName());
        verify(offerRepository).save(offer);
    }

    @Test
    void bookingFullRideReturnsConflict() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(sampleOffer(0)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.book(1L, "Amoha"));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void bookingUnknownRideReturnsNotFound() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.book(99L, "Amoha"));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void driverCannotBookOwnRide() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(sampleOffer(2)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.book(1L, "arjun"));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void sameRiderCannotBookTwice() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(sampleOffer(2)));
        when(bookingRepository.existsByOfferIdAndRiderNameIgnoreCase(1L, "Amoha")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.book(1L, "Amoha"));

        assertEquals(409, ex.getStatusCode().value());
    }
}
