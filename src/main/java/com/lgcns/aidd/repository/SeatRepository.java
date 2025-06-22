package com.lgcns.aidd.repository;

import com.lgcns.aidd.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository  extends JpaRepository<Seat, Long> {

}
