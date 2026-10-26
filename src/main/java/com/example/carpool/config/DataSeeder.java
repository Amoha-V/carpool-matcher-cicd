package com.example.carpool.config;

import com.example.carpool.model.Place;
import com.example.carpool.model.RideOffer;
import com.example.carpool.repository.RideOfferRepository;
import com.example.carpool.service.RideService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Loads a few sample rides for tomorrow so the UI has something to match on first start. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RideOfferRepository repository;
    private final RideService rideService;

    public DataSeeder(RideOfferRepository repository, RideService rideService) {
        this.repository = repository;
        this.rideService = rideService;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        rideService.createOffer(offer("Arjun", Place.TAMBARAM, Place.SNU_CHENNAI, tomorrow, 7, 45, 3));
        rideService.createOffer(offer("Meera", Place.VELACHERY, Place.SNU_CHENNAI, tomorrow, 8, 0, 2));
        rideService.createOffer(offer("Karthik", Place.ADYAR, Place.SNU_CHENNAI, tomorrow, 7, 30, 1));
        rideService.createOffer(offer("Divya", Place.SIRUSERI, Place.SNU_CHENNAI, tomorrow, 8, 15, 2));
        rideService.createOffer(offer("Rahul", Place.SNU_CHENNAI, Place.TAMBARAM, tomorrow, 17, 30, 3));
    }

    private RideOffer offer(String driver, Place from, Place to, LocalDate day, int hour, int minute, int seats) {
        return new RideOffer(driver, from, to, LocalDateTime.of(day, LocalTime.of(hour, minute)), seats);
    }
}
