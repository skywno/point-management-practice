package me.ezra.pm.point;

import me.ezra.pm.point.reservation.PointReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>, PointCustomRepository {
    List<Point> findByExpiredTrue();
}
