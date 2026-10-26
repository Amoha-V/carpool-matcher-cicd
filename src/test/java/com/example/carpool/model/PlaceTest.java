package com.example.carpool.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceTest {

    @Test
    void distanceToSelfIsZero() {
        assertEquals(0.0, Place.TAMBARAM.distanceKm(Place.TAMBARAM), 0.0001);
    }

    @Test
    void distanceIsSymmetric() {
        double ab = Place.ADYAR.distanceKm(Place.SNU_CHENNAI);
        double ba = Place.SNU_CHENNAI.distanceKm(Place.ADYAR);
        assertEquals(ab, ba, 0.0001);
    }

    @Test
    void tambaramToUniversityIsAboutTwentyThreeKm() {
        double km = Place.TAMBARAM.distanceKm(Place.SNU_CHENNAI);
        assertTrue(km > 20 && km < 26, "unexpected distance: " + km);
    }
}
