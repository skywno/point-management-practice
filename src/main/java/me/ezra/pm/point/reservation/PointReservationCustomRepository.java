package me.ezra.pm.point.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointReservationCustomRepository {

    Page<PointReservation> findPointReservationToExecute(LocalDate today, Pageable pageable);

}
