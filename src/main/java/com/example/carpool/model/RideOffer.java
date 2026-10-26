package com.example.carpool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_offers")
public class RideOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "driverName is required")
    @Size(max = 60)
    private String driverName;

    @NotNull(message = "origin is required")
    @Enumerated(EnumType.STRING)
    private Place origin;

    @NotNull(message = "destination is required")
    @Enumerated(EnumType.STRING)
    private Place destination;

    @NotNull(message = "departureTime is required")
    private LocalDateTime departureTime;

    @Min(value = 1, message = "seatsTotal must be at least 1")
    @Max(value = 6, message = "seatsTotal must be at most 6")
    private int seatsTotal;

    private int seatsAvailable;

    public RideOffer() {
    }

    public RideOffer(String driverName, Place origin, Place destination,
                     LocalDateTime departureTime, int seatsTotal) {
        this.driverName = driverName;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.seatsTotal = seatsTotal;
        this.seatsAvailable = seatsTotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Place getOrigin() { return origin; }
    public void setOrigin(Place origin) { this.origin = origin; }

    public Place getDestination() { return destination; }
    public void setDestination(Place destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public int getSeatsTotal() { return seatsTotal; }
    public void setSeatsTotal(int seatsTotal) { this.seatsTotal = seatsTotal; }

    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
}
