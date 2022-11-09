package me.ezra.pm.point;

import me.ezra.pm.point.reservation.PointReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>, PointCustomRepository {
    List<Point> findByExpiredTrue();

}
