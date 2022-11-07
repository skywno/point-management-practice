package me.ezra.pm.message;

import me.ezra.pm.point.reservation.PointReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
