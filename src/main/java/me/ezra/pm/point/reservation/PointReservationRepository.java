package me.ezra.pm.point.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PointReservationRepository extends JpaRepository<PointReservation, Long>, PointReservationCustomRepository {
}
