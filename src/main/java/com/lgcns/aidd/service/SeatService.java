package com.lgcns.aidd.service;

import com.lgcns.aidd.dto.request.SeatAvailabilityRequest;
import com.lgcns.aidd.dto.response.SeatAvailabilityResponse;
import org.springframework.stereotype.Service;

@Service
public interface SeatService {

    public SeatAvailabilityResponse getAvailableSeats(SeatAvailabilityRequest request);
}
