package com.lgcns.aidd.model;

import com.lgcns.aidd.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime checkInAt;

    private LocalDateTime checkOutAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reservation_id")
    private Reservation parentReservation;

    private Boolean isExtend;

    private java.time.LocalTime extendTime;
}
