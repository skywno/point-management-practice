package me.ezra.pm.job.expire;

import me.ezra.pm.BatchTestSupport;
import me.ezra.pm.point.Point;
import me.ezra.pm.point.PointRepository;
import me.ezra.pm.point.wallet.PointWallet;
import me.ezra.pm.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class ExpirePointJobConfigurationTest extends BatchTestSupport {
    @Autowired
    Job expirePointJob;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PointWalletRepository pointWalletRepository;

    @Test
    void expirePointJob() throws Exception {
        // Given
        LocalDate earnDate = LocalDate.of(2021,1,1);
        LocalDate expireDate = LocalDate.of(2021, 1, 6);
        LocalDate ADateAfterExpireDate = expireDate.plusDays(1);

        PointWallet pointWallet = pointWalletRepository.save(new PointWallet("user1",
                BigInteger.valueOf(6000)));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, ADateAfterExpireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, ADateAfterExpireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, ADateAfterExpireDate));

        // When
        JobParameters jobParameters = new JobParametersBuilder().addString("today", "2021-01-07").addDate("date", new Date()).toJobParameters();
        JobExecution jobExecution = launchJob(expirePointJob, jobParameters);

        // Then
        List<Point> points = pointRepository.findAll();
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        then(points.size()).isEqualTo(6);
        then(points.stream().filter(Point::isExpired).count()).isEqualTo(3);
        PointWallet changedPointWallet = pointWalletRepository.findById(pointWallet.getId()).orElseGet(null);
        then(changedPointWallet).isNotNull();
        then(changedPointWallet.getAmount()).isEqualByComparingTo(BigInteger.valueOf(3000));
    }
}