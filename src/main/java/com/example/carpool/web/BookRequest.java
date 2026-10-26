package com.example.carpool.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "riderName is required") @Size(max = 60) String riderName) {
}
