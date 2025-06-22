package com.lgcns.aidd.controller;

import com.lgcns.aidd.dto.request.SeatAvailabilityRequest;
import com.lgcns.aidd.dto.response.SeatAvailabilityResponse;
import com.lgcns.aidd.service.SeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/available")
    public ResponseEntity<SeatAvailabilityResponse> getAvailableSeats(@Valid SeatAvailabilityRequest request) {
        SeatAvailabilityResponse response = seatService.getAvailableSeats(request);
        return ResponseEntity.ok(response);
    }
}
