package com.example.carpool.web;

import com.example.carpool.model.Place;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** A rider's search: where from/to, preferred departure time and how flexible they are. */
public record MatchRequest(
        @NotNull(message = "origin is required") Place origin,
        @NotNull(message = "destination is required") Place destination,
        @NotNull(message = "departureTime is required") LocalDateTime departureTime,
        @Min(0) @Max(240) int flexMinutes) {
}
