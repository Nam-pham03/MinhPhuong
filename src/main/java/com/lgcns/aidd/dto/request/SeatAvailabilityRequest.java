package com.lgcns.aidd.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SeatAvailabilityRequest {
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be today or in the future")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private String startTime; // Format HH:mm

    @NotNull(message = "End time is required")
    private String endTime; // Format HH:mm

    private Integer limit; // Optional, default = 10

    private String buildingCode; // Optional

    private String floorCode; // Optional
}
