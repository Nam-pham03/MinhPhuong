package com.lgcns.aidd.dto.response;
import com.lgcns.aidd.model.Seat;
import lombok.Data;

import java.util.List;

@Data
public class SeatAvailabilityResponse {
    private String status;
    private List<Seat> data;
    private String error;
}