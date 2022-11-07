package me.ezra.pm.point.wallet;

import me.ezra.pm.point.reservation.PointReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointWalletRepository extends JpaRepository<PointWallet, Long> {
}
