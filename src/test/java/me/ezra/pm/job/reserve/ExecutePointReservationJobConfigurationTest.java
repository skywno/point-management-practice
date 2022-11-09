package me.ezra.pm.job.reserve;

import me.ezra.pm.BatchTestSupport;
import me.ezra.pm.point.Point;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.reservation.PointReservation;
import me.ezra.pm.point.reservation.PointReservationRepository;
import me.ezra.pm.point.wallet.PointWallet;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ExecutePointReservationJobConfigurationTest extends BatchTestSupport {

    @Autowired
    PointWalletRepository pointWalletRepository;

    @Autowired
    PointReservationRepository pointReservationRepository;

    @Autowired
    Job executePointReservationJob;

    @Autowired
    PointRepository pointRepository;

    @Test
    void executePointReservationJob() throws Exception {
        // Given
        LocalDate earnDate = LocalDate.of(2022, 11, 5);
        PointWallet pointWallet = pointWalletRepository.save(new PointWallet(
                "pointWallet123", BigInteger.valueOf(3000)));

        pointReservationRepository.save(new PointReservation(pointWallet,
                BigInteger.valueOf(1000), earnDate, 30));

        pointReservationRepository.save(new PointReservation(pointWallet,
                BigInteger.valueOf(500), earnDate.minusDays(1), 30));

        pointReservationRepository.save(new PointReservation(pointWallet,
                BigInteger.valueOf(700), earnDate.plusDays(1), 30));

        // When
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2022-11-05")
                .addDate("date", new Date())
                .toJobParameters();
        JobExecution execution = null;
        execution = launchJob(executePointReservationJob, jobParameters);

        // Then
        then(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Point> points = pointRepository.findAll();
        then(points).hasSize(2);

        // 1000 point
        Point point1 = points.stream()
                .filter(point -> point.getAmount().compareTo(BigInteger.valueOf(1000)) == 0)
                .findAny()
                .orElse(null);

        then(point1).isNotNull();
        then(point1.getEarnedDate()).isEqualTo(LocalDate.of(2022, 1, 5));
        then(point1.getExpireDate()).isEqualTo(LocalDate.of(2022, 2, 4));
        then(point1.isExpired()).isFalse();
        then(point1.isUsed()).isFalse();

        // 500 point
        Point point2 = points.stream()
                .filter(point -> point.getAmount().compareTo(BigInteger.valueOf(500)) == 0)
                .findAny()
                .orElse(null);

        then(point2).isNotNull();
        then(point2.getEarnedDate()).isEqualTo(LocalDate.of(2022, 1, 4));
        then(point2.getExpireDate()).isEqualTo(LocalDate.of(2022, 2, 3));
        then(point2.isExpired()).isFalse();
        then(point2.isUsed()).isFalse();

        // PointWallet의 잔액 확인 3000 -> 4500
        List<PointWallet> wallets = pointWalletRepository.findAll();
        then(wallets).hasSize(1);
        then(wallets.get(0).getAmount()).isEqualByComparingTo(BigInteger.valueOf(4500));

        // reservation 2개 완료처리되었는지 확인
        List<PointReservation> reservations = pointReservationRepository.findAll();
        then(reservations).hasSize(3);
        then(reservations.stream().filter(it -> it.isExecuted())).hasSize(2);
    }

}