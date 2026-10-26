package com.example.carpool.web;

import com.example.carpool.model.Booking;
import com.example.carpool.service.MatchingService;
import com.example.carpool.service.RideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarpoolController.class)
class CarpoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideService rideService;

    @MockBean
    private MatchingService matchingService;

    @Test
    void placesEndpointListsAreas() throws Exception {
        mockMvc.perform(get("/api/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code=='TAMBARAM')]").exists());
    }

    @Test
    void createOfferReturns201() throws Exception {
        when(rideService.createOffer(any())).thenAnswer(inv -> inv.getArgument(0));

        String json = """
                {"driverName":"Arjun","origin":"TAMBARAM","destination":"SNU_CHENNAI",
                 "departureTime":"2026-10-24T07:45:00","seatsTotal":3}
                """;

        mockMvc.perform(post("/api/offers").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverName").value("Arjun"));
    }

    @Test
    void createOfferWithoutDriverReturns400() throws Exception {
        String json = """
                {"origin":"TAMBARAM","destination":"SNU_CHENNAI",
                 "departureTime":"2026-10-24T07:45:00","seatsTotal":3}
                """;

        mockMvc.perform(post("/api/offers").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void matchesEndpointReturnsArray() throws Exception {
        when(matchingService.findMatches(any())).thenReturn(List.of());

        String json = """
                {"origin":"TAMBARAM","destination":"SNU_CHENNAI",
                 "departureTime":"2026-10-24T07:45:00","flexMinutes":30}
                """;

        mockMvc.perform(post("/api/matches").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void bookEndpointReturns201() throws Exception {
        when(rideService.book(eq(1L), eq("Amoha"))).thenReturn(new Booking(1L, "Amoha"));

        mockMvc.perform(post("/api/offers/1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"riderName\":\"Amoha\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riderName").value("Amoha"));
    }
}
