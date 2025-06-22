package com.lgcns.aidd.service.impl;

import com.lgcns.aidd.dto.request.SeatAvailabilityRequest;
import com.lgcns.aidd.dto.response.SeatAvailabilityResponse;
import com.lgcns.aidd.enums.ReservationStatus;
import com.lgcns.aidd.enums.SeatState;
import com.lgcns.aidd.model.Reservation;
import com.lgcns.aidd.model.Seat;
import com.lgcns.aidd.repository.ReservationRepository;
import com.lgcns.aidd.repository.SeatRepository;
import com.lgcns.aidd.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    private final ReservationRepository reservationRepository;

    public SeatAvailabilityResponse getAvailableSeats(SeatAvailabilityRequest request) {
        LocalDate date = request.getDate();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        String buildingCode = request.getBuildingCode();
        String floorCode = request.getFloorCode();


        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime startDateTime = LocalDateTime.of(date, java.time.LocalTime.parse(startTime, timeFormatter));
        LocalDateTime endDateTime = LocalDateTime.of(date, java.time.LocalTime.parse(endTime, timeFormatter));


        if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new IllegalArgumentException("Reservation must be within the same day");
        }
        long hoursDiff = Duration.between(startDateTime, endDateTime).toHours();
        if (hoursDiff > 8 || hoursDiff <= 0) {
            throw new IllegalArgumentException("Reservation duration must be between 1 and 8 hours");
        }

        // Fetch seats
        List<Seat> allSeats = seatRepository.findAll();
        if (buildingCode != null || floorCode != null) {
            allSeats = allSeats.stream()
                    .filter(seat -> (buildingCode == null || seat.getFloor().getBuilding().getName().equals(buildingCode))
                            && (floorCode == null || seat.getFloor().getFloor().toString().equals(floorCode)))
                    .collect(Collectors.toList());
        }

        // Filter available seats
        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> SeatState.USABLE.equals(seat.getState()))
                .filter(seat -> {
                    List<Reservation> reservations = reservationRepository.findBySeatId(seat.getId());
                    return reservations.stream().noneMatch(reservation ->
                            (reservation.getStatus() == ReservationStatus.RESERVED ||
                                    reservation.getStatus() == ReservationStatus.IN_USE) &&
                                    isTimeOverlap(startDateTime, endDateTime, reservation.getStartTime(), reservation.getEndTime())
                    );
                })
                .limit(limit)
                .collect(Collectors.toList());

        // Build response
        SeatAvailabilityResponse response = new SeatAvailabilityResponse();
        response.setStatus("success");
        response.setData(availableSeats);
        return response;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }


}
